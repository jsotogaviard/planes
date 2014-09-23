package com.qfs.planes.cfg;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value="classpath:planes.properties")
@Configuration
@Import(value={
		SparkseeConfig.class,
		CypherEngineServiceConfig.class
})
public class PlanesConfig {}