package com.jsoto.planes.data.impl;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;


public class Flight extends ACsvWritable{
	
	public static final String NAME = Flight.class.getSimpleName().toLowerCase();

	public static final String ID = NAME + "Id";
	
	public static final Label LABEL = DynamicLabel.label(NAME);
	
	public static final String[] PROPERTIES = new String[]{"planeId","from","to","STD","ETD","STA","ETA"};
	
	public Flight() {
		super(Flight.class);
	}
	
	public Flight(String fligthId, String planeId, String from, String to, String sTD, String eTD, String sTA, String eTA) {
		super(Flight.class);
		this.flightId = fligthId;
		this.planeId = planeId;
		this.from = from;
		this.to = to;
		STD = sTD;
		ETD = eTD;
		STA = sTA;
		ETA = eTA;
	}

	protected String flightId;
	
	protected String planeId;
	
	protected String from;
	
	protected String to;
	
	/** Standard time of departure */
	protected String STD;
	
	/** Estimated time of arrival */
	protected String ETD;
	
	/** Standard time of arrival */
	protected String STA;
	
	/** Estimated time of arrival */
	protected String ETA;

}
