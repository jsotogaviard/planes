package com.jsoto.planes.data.impl;

public class Passenger extends ACsvWritable{
	
	public static String ID = "passengerId";	
	
	public static String[] PROPERTIES = new String[]{"fligthId","itineraryId","luggage"};
	
	public Passenger() {
		super(Passenger.class);
	}
	
	public Passenger(String passengerId, String fligthId, String itineraryId, double luggage) {
		super(Passenger.class);
		this.passengerId = passengerId;
		this.fligthId = fligthId;
		this.itineraryId = itineraryId;
		this.luggage = luggage;
	}

	protected String passengerId;
	
	protected String fligthId;
	
	protected String itineraryId;
	
	protected double luggage;

}
