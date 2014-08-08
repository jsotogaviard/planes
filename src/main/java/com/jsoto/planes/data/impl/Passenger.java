package com.jsoto.planes.data.impl;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

public class Passenger extends ACsvWritable{
	
	public static final String NAME = Passenger.class.getSimpleName().toLowerCase();

	public static final String ID = NAME + "Id";
	
	public static final Label LABEL = DynamicLabel.label(NAME);
	
	public static String[] PROPERTIES = new String[]{"flightId","itineraryId","luggage"};
	
	public Passenger() {
		super(Passenger.class);
	}
	
	public Passenger(String passengerId, String fligthId, String itineraryId, double luggage) {
		super(Passenger.class);
		this.passengerId = passengerId;
		this.flightId = fligthId;
		this.itineraryId = itineraryId;
		this.luggage = luggage;
	}

	protected String passengerId;
	
	protected String flightId;
	
	protected String itineraryId;
	
	protected double luggage;

}
