package com.qfs.planes.data.impl;

import java.util.List;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

public class Itinerary extends ACsvWritable{
	
	public static final String NAME = Itinerary.class.getSimpleName().toLowerCase();

	public static final String ID = NAME + "Id";
	
	public static final Label LABEL = DynamicLabel.label(NAME);
	
	public static String[] PROPERTIES = new String[]{"personId","from","to","legs"};
	
	public Itinerary() {
		super(Itinerary.class);
	}

	public Itinerary(String itineraryId, String personId, String from, String to, List<String> legs) {
		super(Itinerary.class);
		this.itineraryId = itineraryId;
		this.personId = personId;
		this.from = from;
		this.to = to;
		this.legs = legs;
	}

	protected String itineraryId;
	
	protected String personId;
	
	protected String from;
	
	protected String to;
	
	protected List<String> legs;
}

