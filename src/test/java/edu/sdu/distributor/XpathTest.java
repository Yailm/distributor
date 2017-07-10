package edu.sdu.distributor;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import us.codecraft.xsoup.Xsoup;

public class XpathTest {
	
	private String channel_url = "http://yao.dxy.cn/tag/policy/";
	private String regex_pattern = "href=\"(.*)\" title=\"下一页";
	private String url_xpath = "//p[@class='title']/a/@href";
	private String title_xpath = "//h1[@class='title']/text()";
	private String author_xpath = "//p[@class='x_detail1_author']/text()";
	private String pubtime_xpath = "//div[@class='sum']/span[1]/text()";
	private String source_xpath = "//div[@class='sum']/span[2]/text()";
	private String content_xpath = "//div[@id='content']/allText()";
	
	public static void main(String[] args) {
		XpathTest me = new  XpathTest();
		List<String> list = me.getUrlList();
		try {
			for (int index = 0; index < list.size(); index ++) {
				Document doc = Jsoup.connect(list.get(index)).get();
				me.getTitle(doc);
				me.getAuthor(doc);
				me.getPubtime(doc);
				me.getSource(doc);
				me.getContent(doc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> getUrlList() {
		Document doc;
		try {
			doc = Jsoup.connect(channel_url).get();
			List<String> result = Xsoup.compile(url_xpath).evaluate(doc).list();
			
			Matcher matcher = Pattern.compile(regex_pattern).matcher(doc.html());
			matcher.find();
			System.out.println(matcher.group(1));
			for (int i = 0; i < result.size(); i++) {
				System.out.println(result.get(i));
			}
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void getTitle(Document doc) {
		String title = Xsoup.compile(title_xpath).evaluate(doc).get();
		System.out.println("标题："+title);
	}
	
	public void getPubtime(Document doc) {
		String pubtime = Xsoup.compile(pubtime_xpath).evaluate(doc).get();
		//TODO 时间需要处理一下
		System.out.println("发表时间："+pubtime);
	}
	
	public void getAuthor(Document doc) {
		String author = Xsoup.compile(author_xpath).evaluate(doc).get();
		System.out.println("记者："+author);
	}
	
	public void getContent(Document doc) {
		String content = Xsoup.compile(content_xpath).evaluate(doc).get();
		System.out.println("内容："+content);
	}
	
	public void getSource(Document doc) {
		String source = Xsoup.compile(source_xpath).evaluate(doc).get();
		System.out.println("来源："+source);
	}
}
