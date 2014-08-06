package com.jsoto.planes.data.impl;

public class Person extends ACsvWritable{
	
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
