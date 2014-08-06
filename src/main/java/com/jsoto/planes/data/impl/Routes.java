package com.jsoto.planes.data.impl;

import java.util.List;

public class Routes extends ACsvWritable{
	
	public Routes() {
		super(Routes.class);
	}
	
	protected String from;
	
	protected String to;
	
	protected List<String> route;

}
