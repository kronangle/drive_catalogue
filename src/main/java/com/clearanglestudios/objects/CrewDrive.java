package com.clearanglestudios.objects;

public class CrewDrive {
	private final String driveName;
    private final String driveSize;
    private final String driveStatus;
    private final String driveDetails;
    private final String driveDate;
    private final String driveLogger;
    
	public CrewDrive(String driveName, String driveSize, String driveStatus, String driveDetails, String driveDate,
			String driveLogger) {
		this.driveName = driveName;
		this.driveSize = driveSize;
		this.driveStatus = driveStatus;
		this.driveDetails = driveDetails;
		this.driveDate = driveDate;
		this.driveLogger = driveLogger;
	}

//	============================================
//	
//					Getters
//	
//	============================================
	
	public String getDriveName() {
		return driveName;
	}

	public String getDriveSize() {
		return driveSize;
	}

	public String getDriveStatus() {
		return driveStatus;
	}

	public String getDriveDetails() {
		return driveDetails;
	}

	public String getDriveDate() {
		return driveDate;
	}

	public String getDriveLogger() {
		return driveLogger;
	}
}
