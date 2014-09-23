/*
 * (C) Quartet FS 2007-2014
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.qfs.planes.rest;

/**
 *
 * @author Quartet FS
 */
public class CypherQuery {
	
	public String cypher;
	
	public CypherQuery() {}
	
	

	/** Constructor
	 * @param cypher
	 */
	public CypherQuery(String cypher) {
		super();
		this.cypher = cypher;
	}



	/**
	 * @return The cypher
	 */
	public String getCypher() {
		return this.cypher;
	}

	/**
	 * Sets the cypher
	 * @param cypher The cypher to set
	 */
	public void setCypher(String cypher) {
		this.cypher = cypher;
	}
	
	

}
