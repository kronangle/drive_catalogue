package com.clearanglestudios.objects;

public class User {
	
	private String googleAccount;
	private String token;
	private String name;
	private String department;
	private String email;
	
	public User(String googleAccount, String token, String name, String department, String email) {
		this.googleAccount = googleAccount;
		this.token = token;
		this.name = name;
		this.department = department;
		this.email = email;
	}
	
//	============================================
//	
//				Getters and Setters
//	
//	============================================	
	
	public String getGoogleAccount() {
		return googleAccount;
	}
	public void setGoogleAccount(String googleAccount) {
		this.googleAccount = googleAccount;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
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
