package com.qfs.planes.data.impl;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

public class Person extends ACsvWritable{
	
	public static final String NAME = Person.class.getSimpleName().toLowerCase();

	public static final String ID = NAME + "Id";
	
	public static final Label LABEL = DynamicLabel.label(NAME);
	
	public static String[] PROPERTIES = new String[]{"surname","name"};
	
	public Person() {
		super(Person.class);
	}
	
	public Person(String personId, String surname, String name) {
		super(Person.class);
		this.personId = personId;
		this.surname = surname;
		this.name = name;
	}

	protected String personId;
	
	protected String surname;
	
	protected String name;

}
