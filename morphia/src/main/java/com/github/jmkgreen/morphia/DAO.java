package com.github.jmkgreen.morphia;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import com.mongodb.Mongo;

@Deprecated //use dao.BasicDAO
public class DAO<T, K> extends BasicDAO<T, K> {
	public DAO(Class<T> entityClass, Mongo mongo, Morphia morphia, String dbName) {
		super(entityClass, mongo, morphia, dbName);
	}
	
	public DAO(Class<T> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	
}
