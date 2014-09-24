package edu.upc.dama.planes.actions;

public class User {

	protected String name;
	
	protected String clazz;

	public User() {}
	
	public User(String name, String clazz) {
		super();
		this.name = name;
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	
	
}
