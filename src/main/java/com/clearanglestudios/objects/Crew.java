package com.clearanglestudios.objects;

public class Crew {

	private String name;
	private String department;
	private String email;

	public Crew(String name, String email, String department) {
		this.name = name.trim();
		this.department = department;
		this.email = email;
	}

//	============================================
//	
//				Getters and Setters
//	
//	============================================
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
