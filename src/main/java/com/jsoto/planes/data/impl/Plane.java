package com.jsoto.planes.data.impl;

public class Plane extends ACsvWritable{
	
	public Plane() {
		super(Plane.class);
	}
	
	protected String planeId;
	
	protected int businessSeats;

	protected int economySeats;
	
	protected double baggageCapacity;
}


