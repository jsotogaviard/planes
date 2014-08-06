package com.jsoto.planes.data.impl;


public class Flight extends ACsvWritable{
	
	public Flight() {
		super(Flight.class);
	}
	
	public Flight(String fligthId, String planeId, String from, String to, String sTD, String eTD, String sTA, String eTA) {
		super(Flight.class);
		this.fligthId = fligthId;
		this.planeId = planeId;
		this.from = from;
		this.to = to;
		STD = sTD;
		ETD = eTD;
		STA = sTA;
		ETA = eTA;
	}

	protected String fligthId;
	
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
