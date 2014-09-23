package com.qfs.planes.rest;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

public class CypherService {
	
	protected ExecutionEngine engine;
	private GraphDatabaseService db;
	
	public CypherService(ExecutionEngine engine, GraphDatabaseService db) {
		this.engine = engine;
		this.db = db;
	}
	
	public String query(String query) {
		String r = null;
		try {
			try (Transaction ignored = db.beginTx()){
				ExecutionResult in = engine.execute(query);
				r = in.dumpToString();
			}
		} catch (Exception ex) {
			throw ex;
		}
		return r;
	}

}
