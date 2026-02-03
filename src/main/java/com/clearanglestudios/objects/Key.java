package com.clearanglestudios.objects;

public class Key {

	private String casTag; // Col A
	private String itemName; // Col B
	private String rig; // Col C
	private String status; // Col D (In, Out, Unknown)
	private String dateOfLog; // Col E
	private String loggedBy; // Col F
	private String assignedTo; // Col G
	private String notes; // Col H
	
	private int rowIndex; // To track where this key lives in the sheet

	public Key(int rowIndex, String casTag, String itemName, String rig, String status, String dateOfLog,
			String loggedBy, String assignedTo, String notes) {
		this.rowIndex = rowIndex;
		this.casTag = casTag;
		this.itemName = itemName;
		this.rig = rig;
		this.status = status;
		this.dateOfLog = dateOfLog;
		this.loggedBy = loggedBy;
		this.assignedTo = assignedTo;
		this.notes = notes;
	}

	// --- Getters and Setters ---

	public String getCasTag() {
		return casTag;
	}

	public void setCasTag(String casTag) {
		this.casTag = casTag;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getRig() {
		return rig;
	}

	public void setRig(String rig) {
		this.rig = rig;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDateOfLog() {
		return dateOfLog;
	}

	public void setDateOfLog(String dateOfLog) {
		this.dateOfLog = dateOfLog;
	}

	public String getLoggedBy() {
		return loggedBy;
	}

	public void setLoggedBy(String loggedBy) {
		this.loggedBy = loggedBy;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	@Override
	public String toString() {
		return itemName + " (" + casTag + ")"; 
	}
}