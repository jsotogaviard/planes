package com.jsoto.planes.data.impl;

public class Passenger extends ACsvWritable{
	
	public Passenger() {
		super(Passenger.class);
	}
	
	public Passenger(String fligthId, String itineraryId, double luggage) {
		super(Passenger.class);
		this.fligthId = fligthId;
		this.itineraryId = itineraryId;
		this.luggage = luggage;
	}

	protected String fligthId;
	
	protected String itineraryId;
	
	protected double luggage;

}
