package com.qfs.planes.cfg;

import java.io.File;
import java.util.Arrays;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import com.qfs.planes.graph.PlaneGraph;
import com.qfs.planes.rest.CypherRestService;
import com.qfs.planes.rest.CypherService;
import com.qfs.planes.util.PlanesUtil;

@Configuration
public class CypherEngineServiceConfig {

	@Autowired
	protected Environment env;

	@Bean
	public GraphDatabaseService graph() {
		String graph = env.getProperty("graph");
		String data = env.getProperty("data");
		PlanesUtil.deleteFolder(new File(graph));
		PlaneGraph g = new PlaneGraph();
		return  g.createGraph(graph, data);
	}

	@Bean
	public ExecutionEngine executionEngine() {
		return new ExecutionEngine(graph());
	}

	// Rest
	@Bean( destroyMethod = "shutdown" )
	public SpringBus cxf() {
		return new SpringBus();
	}

	@Bean
	@DependsOn("cxf")
	public Server jaxRsServer() {
		JAXRSServerFactoryBean factory = RuntimeDelegate.getInstance().createEndpoint( jaxRsKpiApplication(), JAXRSServerFactoryBean.class );
		factory.setServiceBeans( Arrays.< Object >asList(
				cypherRestService()
				));
		factory.setProviders( Arrays.< Object >asList( jsonProvider() ) );
		return factory.create();
	}

	@Bean
	public CypherRestService cypherRestService() {
		return new CypherRestService(cypherService());
	}
	
	@Bean
	public CypherService cypherService() {
		return new CypherService(executionEngine(), graph());
	}

	@Bean
	public Application jaxRsKpiApplication() {
		return new Application();
	}


	@Bean
	public JacksonJsonProvider jsonProvider() {
		return new JacksonJsonProvider();
	}

}
