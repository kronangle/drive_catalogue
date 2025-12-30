package com.clearanglestudios.objects;

public class DriveProperty {
    private final String propertyName;
    private final String propertyValue;

    public DriveProperty(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }
    
//	============================================
//	
//					Getters
//	
//	============================================

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
}