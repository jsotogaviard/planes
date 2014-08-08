package com.jsoto.planes.cfg;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class WebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		// Spring Context Bootstrapping
		servletContext.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
		servletContext.setInitParameter("contextConfigLocation", PlanesConfig.class.getName());
		servletContext.addListener(new ContextLoaderListener());
		
		Dynamic rest = servletContext.addServlet("Rest Servlet", new CXFServlet());
		rest.addMapping("/rest/*");
		rest.setLoadOnStartup(1);

	}

}
