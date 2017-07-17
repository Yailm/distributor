package edu.sdu.distributor;


import java.util.List;
import java.util.Map;

import edu.sdu.distributor.model.TemplateInfo;

public interface MysqlApproaches {
	TemplateInfo getTemplate(int id);
	List<TemplateInfo> getAllTemplates();
	void setStatus(Map<String, Integer> map);
}