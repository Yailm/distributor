package edu.sdu.distributor;


import java.util.List;
import java.util.Map;

import edu.sdu.distributor.model.TemplateInfo;

public interface MysqlApproaches {
	public TemplateInfo getTemplate(int id);
	public List<TemplateInfo> getAllTemplates();
	public void setStatus(Map<String, Integer> map);
}