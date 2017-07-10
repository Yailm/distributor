package edu.sdu.distributor;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import edu.sdu.distributor.model.TemplateInfo;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class TaskDistributor {

	private Map<String, DelayTask> workersTask = new HashMap<String, DelayTask>();
	private DelayQueue<DelayTask> queue = new DelayQueue<>();
	private Map<Integer, TemplateInfo> templates = new HashMap<>();
	private SqlSession sqlSession;

	public static void main(String[] args) {

		TaskDistributor me = new TaskDistributor();

		staticFiles.location("/");
		staticFiles.expireTime(600);
		port(6080);
		get("/task/:status", (request, response) -> {
			response.type("application/json");

			String field = DigestUtils.sha1Hex(request.ip());
			int status = Integer.parseInt(request.params(":status"));
			if (me.workersTask.containsKey(field)) {
				DelayTask preivousTask = me.workersTask.remove(field);
				if (status != 0)
					me.setStatus(preivousTask.getTemplate(), status);
				preivousTask.finish();
			}
			DelayTask task;
			if ((task = me.queue.poll()) != null) {
				// 获得可使用的模板
				if (task.getCrawlerIP().length() > 0)
					me.workersTask.remove(DigestUtils.sha1Hex(task.getCrawlerIP()));
				me.queue.offer(task.refresh(request.ip()));
				me.workersTask.put(field, task);
				return JSON.toJSONString(task.getTemplate());
			}
			return "{\"status\": 1}";
		});

		get("/status", (request, response) -> {
			return new ModelAndView(null, "status.ftl");
		}, new FreeMarkerEngine());

		get("/json/stats.json", (request, response) -> {
			response.type("application/json");
			Map<String, Object> model = new HashMap<>();
			Iterator<DelayTask> iterator = me.queue.iterator();
			List<Object> taskList = new ArrayList<>();
			model.put("tasks", taskList);
			while (iterator.hasNext()) {
				DelayTask task = iterator.next();
				TemplateInfo templateInfo = task.getTemplate();
				Map<String, Object> taskItem = new HashMap<>();
				taskItem.put("id", templateInfo.getId());
				taskItem.put("site", templateInfo.getWebsite_name());
				taskItem.put("url", templateInfo.getChannel_url());
				taskItem.put("count", task.getCount());
				taskItem.put("status", templateInfo.getStatus());
				taskItem.put("progress", task.getProgress());
				taskItem.put("crawler", task.getCrawlerIP());
				taskList.add(taskItem);
			}
			Collections.reverse(taskList);
			model.put("updated", System.nanoTime());
			return JSON.toJSONString(model);
		});

		get("/update/:id", (request, response) -> {
			// 懒更新数据
			response.type("application/json");
			int id = Integer.parseInt(request.params(":id"));
			return "{\"status\": \"" + me.updateFromMysql(id) + "\"}";
		});
	}

	public TaskDistributor() {
		updateFromMysql();
	}

	private void setStatus(TemplateInfo templateInfo, int status) {
		templateInfo.setStatus(status); // in memory

		Map<String, Integer> map = new HashMap<>(); // in mysql
		map.put("id", templateInfo.getId());
		map.put("status", status);
		try {
			approach().setStatus(map);
			sqlSession.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sqlSession.close();
		}
	}

	private void updateFromMysql() {
		try {
			Iterator<TemplateInfo> iterator = approach().getAllTemplates().iterator();
			while (iterator.hasNext()) {
				TemplateInfo templateInfo = iterator.next();
				templates.put(templateInfo.getId(), templateInfo);
				queue.offer(new DelayTask(templateInfo));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sqlSession.close();
		}
	}

	private MysqlApproaches approach() {
		sqlSession = ModelManager.me().getFactory().openSession();
		return sqlSession.getMapper(MysqlApproaches.class);
	}

	private String updateFromMysql(int id) {
		try {
			Logger logger = Logger.getLogger(TaskDistributor.class);
			TemplateInfo stuff = approach().getTemplate(id);
			logger.info(JSON.toJSONString(stuff));
			if (stuff == null) {
				if ((stuff = templates.remove(id)) != null) {
					Iterator<DelayTask> iterator = queue.iterator();
					while (iterator.hasNext()) {
						DelayTask task = iterator.next();
						if (task.getTemplate() == stuff) {
							workersTask.remove(DigestUtils.sha1Hex(task.getCrawlerIP()));
							queue.remove(task);
							break;
						}
					}
					return "deleted";
				}
				return "nothing to do";
			}
			if (templates.containsKey(id)) {
				try {
					BeanUtils.copyProperties(templates.get(id), stuff);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "failed";
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "failed";
				}
				return "updated";
			}
			templates.put(id, stuff);
			queue.offer(new DelayTask(stuff));
			return "added";
		} finally {
			sqlSession.close();
		}
	}

	class DelayTask implements Delayed {
		private long plannedTime;
		private TemplateInfo templateInfo;
		private int count = 0;
		private String crawlerIP = "";
		private final static long INTERVAL = 10; // 10 minute

		public DelayTask(TemplateInfo templateInfo) {
			this.templateInfo = templateInfo;
			this.plannedTime = System.nanoTime();
		}

		public DelayTask refresh(String ip) {
			plannedTime = TimeUnit.NANOSECONDS.convert(2 * INTERVAL, TimeUnit.MINUTES) + System.nanoTime();
			crawlerIP = ip;
			return this;
		}

		public void finish() {
			plannedTime = TimeUnit.NANOSECONDS.convert(INTERVAL, TimeUnit.MINUTES) + System.nanoTime();
			crawlerIP = "";
			count++;
		}

		@Override
		public int compareTo(Delayed o) {
			long othersDelay = ((DelayTask) o).getDelay(TimeUnit.NANOSECONDS);
			if (othersDelay > getDelay(TimeUnit.NANOSECONDS))
				return -1;
			else
				return 1;
		}

		@Override
		public long getDelay(TimeUnit unit) {
			if (templateInfo.getStatus() > 0) // 弃用有错误的模板，让其永远超时
				return templateInfo.getStatus();
			return unit.convert(plannedTime - System.nanoTime(), TimeUnit.NANOSECONDS);
		}

		public Map<String, String> getProgress() {
			Map<String, String> result = new HashMap<>();
			long seconds = TimeUnit.SECONDS.convert(plannedTime - System.nanoTime(), TimeUnit.NANOSECONDS);
			result.put("width", "100%");
			if (templateInfo.getStatus() > 0)
				result.put("html", "Error:" + templateInfo.getStatus());
			else if (seconds < 0)
				result.put("html", "Ready");
			else {
				result.put("html", String.format("%02d:%02d", seconds / 60, seconds % 60));
				result.put("width", 5 * seconds / (INTERVAL * 6) + "%");
			}
			return result;
		}

		public String getCrawlerIP() {
			return crawlerIP;
		}

		public int getCount() {
			return count;
		}

		public TemplateInfo getTemplate() {
			return templateInfo;
		}
	}
}
