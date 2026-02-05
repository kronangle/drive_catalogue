package com.clearanglestudios.objects;

public class Drive {
	
	private String serialNumber;
	private String casTag;
	private String driveTag;
	private String name;
	private String size;
	private String status;
	private String assignment;
	private String crew;
	private String dateOfLastLogged;
	private String loggedByUser;
	private String dueDate;
	
	public Drive(String serialNumber, String name, String size, String status, String assignment,
			String crew, String dateOfLastLogged, String loggedByUser, String driveTag, String casTag, String dueDate) {
		this.serialNumber = serialNumber;
		this.casTag = casTag;
		this.driveTag = driveTag;
		this.name = name.toLowerCase();
		this.size = size.substring(0, 1) + "TB";
		this.status = status.toLowerCase().trim();
		this.assignment = assignment;
		this.crew = crew;
		this.dateOfLastLogged = dateOfLastLogged;
		this.loggedByUser = loggedByUser;
		this.dueDate = dueDate;
	}
	
//	============================================
//	
//				Getters and Setters
//	
//	============================================
	
	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getCasTag() {
		return casTag;
	}
	public void setCasTag(String casTag) {
		this.casTag = casTag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAssignment() {
		return assignment;
	}
	public void setAssignment(String assignment) {
		this.assignment = assignment;
	}
	public String getCrew() {
		return crew;
	}
	public void setCrew(String crew) {
		this.crew = crew;
	}
	public String getDateOfLastLogged() {
		return dateOfLastLogged;
	}
	public void setDateOfLastLogged(String dateOfLastLogged) {
		this.dateOfLastLogged = dateOfLastLogged;
	}

	public String getDriveTag() {
		return driveTag;
	}

	public void setDriveTag(String driveTag) {
		this.driveTag = driveTag;
	}

	public String getLoggedByUser() {
		return loggedByUser;
	}

	public void setLoggedByUser(String loggedByUser) {
		this.loggedByUser = loggedByUser;
	}

	@Override
	public String toString() {
		return "Drive [serialNumber=" + serialNumber + ", casTag=" + casTag + ", driveTag=" + driveTag + ", name="
				+ name + ", size=" + size + ", status=" + status + ", assignment=" + assignment + ", crew=" + crew
				+ ", dateOfLastLogged=" + dateOfLastLogged + ", loggedByUser=" + loggedByUser + "]";
	}
	
	

}
