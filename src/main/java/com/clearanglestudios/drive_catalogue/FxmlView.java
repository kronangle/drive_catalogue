package com.clearanglestudios.drive_catalogue;

public enum FxmlView {
    
    HOME("Primary"),
    ASSIGN("DriveAssign"),
    INGEST("DriveIngest"),
    RETURN("DriveReturn"),
    QUERY("DriveQuery"),
    LOGIN("LoginPage");

    private final String fxmlFileName;

    FxmlView(String fxmlFileName) {
        this.fxmlFileName = fxmlFileName;
    }

    public String getFxmlName() {
        return fxmlFileName;
    }
}


