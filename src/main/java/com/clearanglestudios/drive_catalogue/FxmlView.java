package com.clearanglestudios.drive_catalogue;

public enum FxmlView {
    
    HOME("Primary"),
    ASSIGN("DriveAssign"),
    INGEST("DriveIngest"),
    RETURN("DriveReturn"),
    QUERY("DriveQuery"),
    SPLASH("SplashScreen"),
    LOGIN("LoginPage"), 
    KEY_ASSIGN("KeyAssign"),
	KEY_RETURN("KeyReturn"),
	KEY_HOME("KeyHome");

    private final String fxmlFileName;

    FxmlView(String fxmlFileName) {
        this.fxmlFileName = fxmlFileName;
    }

    public String getFxmlName() {
        return fxmlFileName;
    }
}


