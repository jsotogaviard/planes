package com.qfs.planes.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.qfs.sparksee.SpaskseeServer;

@Configuration
public class SparkseeConfig {

		@Autowired
		protected Environment env;

		@Bean
		public SpaskseeServer graph() {
//			String graph = env.getProperty("graph");
//			String data = env.getProperty("data");
//			PlanesUtil.deleteFolder(new File(graph));
//			PlaneGraph g = new PlaneGraph();
			SpaskseeServer sparksee = new SpaskseeServer();
			return sparksee;
		}


}
