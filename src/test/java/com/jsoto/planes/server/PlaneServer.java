package com.jsoto.planes.server;


import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.WebApplicationInitializer;

import com.qfs.planes.cfg.WebAppInitializer;

public class PlaneServer {

	/** Root of the web application files, defined relatively to the project root */
	public static final String WEBAPP = "src/main/webapp";

	/** Jetty server default port (9090) */
	public static final int DEFAULT_PORT = 9090;

	/** Create and configure a Jetty Server */
	public static Server createServer(int port) {

        WebAppContext root = new WebAppContext();
        root.setConfigurations(new Configuration[] { new JettyAnnotationConfiguration() });
        root.setContextPath("/");
        root.setParentLoaderPriority(true);

        // Enable GZIP compression
		FilterHolder gzipFilter = new FilterHolder(org.eclipse.jetty.servlets.GzipFilter.class);
		gzipFilter.setInitParameter("mimeTypes", "text/xml,application/x-java-serialized-object");
		root.addFilter(gzipFilter, "/*", EnumSet.of(DispatcherType.REQUEST));

		// Create server and configure it
		Server server = new Server(port);
		server.setHandler(root);

		return server;
	}


	/**
	 * Configure and launch the standalone server.
	 * @param args only one optional argument is supported: the server port
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		int port = DEFAULT_PORT;
		if(args != null && args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}

		final Server server = createServer(port);

		// Launch the server
		server.start();
		server.join();
	}

	public static class JettyAnnotationConfiguration extends AnnotationConfiguration {

        @Override
        public void preConfigure(WebAppContext context) throws Exception {
        	ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        	ConcurrentHashMap<String, ConcurrentHashSet<String>> map = new ClassInheritanceMap();
        	set.add(WebAppInitializer.class.getName());
            map.put(WebApplicationInitializer.class.getName(), set);
            context.setAttribute(CLASS_INHERITANCE_MAP, map);
            _classInheritanceHandler = new ClassInheritanceHandler(map);
        }

	}


}