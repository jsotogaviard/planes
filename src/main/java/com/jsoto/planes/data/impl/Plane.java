package com.jsoto.planes.data.impl;

public class Plane extends ACsvWritable{
	
	
	public static String ID = "planeId";	
	
	public static String[] PROPERTIES = new String[]{"businessSeats","economySeats","baggageCapacity"};
	
	public Plane() {
		super(Plane.class);
	}
	
	public Plane(String planeId, int businessSeats,	int economySeats, double baggageCapacity) {
		super(Plane.class);
		this.planeId = planeId;
		this.businessSeats = businessSeats;
		this.economySeats = economySeats;
		this.baggageCapacity = baggageCapacity;
	}

	protected String planeId;
	
	protected int businessSeats;

	protected int economySeats;
	
	protected double baggageCapacity;
}


