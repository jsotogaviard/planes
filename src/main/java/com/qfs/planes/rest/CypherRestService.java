package com.qfs.planes.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@Path("/query")
public class CypherRestService {
	
	private static Logger logger = Logger.getLogger(CypherRestService.class.getName());

	protected final CypherService cypherService;

	public CypherRestService(CypherService cypherService) {
		this.cypherService = cypherService;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response query(CypherQuery cypherQuery) {
		try {
			String result = cypherService.query(cypherQuery.getCypher());
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			logger.log(Level.WARNING, "", e);
			return Response.status(Status.NOT_FOUND).entity(e).build();
		}
		
	}

}
