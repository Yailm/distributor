package edu.sdu.distributor;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

class ModelManager {

	private final static ModelManager modelManager ;
	private final static SqlSessionFactory sqlSessionFactory;
	
	static {
		modelManager = new ModelManager();
		String resource = "mybatis-config.xml";
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
	}
	static ModelManager me() {
		return modelManager;
	}
	
	SqlSessionFactory getFactory() {
		return sqlSessionFactory;
	}
}
