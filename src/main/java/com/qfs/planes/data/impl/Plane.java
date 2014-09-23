package com.qfs.planes.data.impl;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

public class Plane extends ACsvWritable{
	
	public static final String NAME = Plane.class.getSimpleName().toLowerCase();

	public static final String ID = NAME + "Id";
	
	public static final Label LABEL = DynamicLabel.label(NAME);
	
	public static String[] PROPERTIES = new String[]{"businessSeats","economySeats","baggageCapacity"};
	
	public Plane() {
		super(Plane.class);
	}
	
	public Plane(String planeId, int businessSeats,	int economySeats, double baggageCapacity, double takeOffWeight) {
		super(Plane.class);
		this.planeId = planeId;
		this.businessSeats = businessSeats;
		this.economySeats = economySeats;
		this.baggageCapacity = baggageCapacity;
		this.takeOffWeight = takeOffWeight;
	}

	protected String planeId;
	
	protected Integer businessSeats;

	protected Integer economySeats;
	
	protected Double baggageCapacity;
	
	protected Double takeOffWeight;
}


