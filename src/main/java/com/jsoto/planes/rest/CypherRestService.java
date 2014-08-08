package com.jsoto.planes.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/query")
public class CypherRestService {

	protected final CypherService cypherService;

	public CypherRestService(CypherService cypherService) {
		this.cypherService = cypherService;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String query(CypherQuery cypherQuery) {
		return cypherService.query(cypherQuery.getCypher());
	}

}
