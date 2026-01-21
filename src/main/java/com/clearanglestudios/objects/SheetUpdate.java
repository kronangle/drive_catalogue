package com.clearanglestudios.objects;

/**
 * A simple data carrier (POJO) that holds the information 
 * needed to update a row in the spreadsheet.
 */
public class SheetUpdate {
    
    private final String[] formInfo;

    public SheetUpdate(String[] formInfo) {
        this.formInfo = formInfo;
    }

    public String[] getInfo() {
        return formInfo;
    }
}