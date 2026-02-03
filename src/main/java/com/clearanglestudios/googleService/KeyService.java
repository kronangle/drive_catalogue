package com.clearanglestudios.googleService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.GoogleTools;
import com.clearanglestudios.objects.Key;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import javafx.application.Platform;

public class KeyService {

	private static final Logger logger = LogManager.getLogger(KeyService.class);

	// Schema Configuration
	private static final String SPREADSHEET_ID = "1eN08KqAWKNvTYG3fCvpxz0kT3VFTDo__S3QdiJvlNN8";
	private static final String SHEET_NAME = "Sign_out_sheet";
	private static final String DATA_RANGE = "Sign_out_sheet!A2:H";
	private static final String HISTORY_SHEET_NAME = "History";

	/**
	 * Fetches all keys from the spreadsheet.
	 */
	public static List<Key> getAllKeys() throws IOException, GeneralSecurityException {
		List<Key> keys = new ArrayList<>();

		logger.info("Fetching Key inventory...");
		Sheets service = GoogleTools.getSheetsService();

		ValueRange response = service.spreadsheets().values().get(SPREADSHEET_ID, DATA_RANGE).execute();

		List<List<Object>> values = response.getValues();

		if (values == null || values.isEmpty()) {
			logger.warn("No data found in Key sheet.");
			return keys;
		}

		// 2. Parse Raw Data into Key Objects
		// Start index at 2 because the range starts at A2 (Row 2)

		int currentRowIndex = 2;

		for (List<Object> row : values) {
			String casTag = cleanRowData(row, 0);
			String itemName = cleanRowData(row, 1);
			String rig = cleanRowData(row, 2);
			String status = cleanRowData(row, 3);
			String dateOfLog = cleanRowData(row, 4);
			String loggedBy = cleanRowData(row, 5);
			String assignedTo = cleanRowData(row, 6);
			String notes = cleanRowData(row, 7);

			// Create Object
			Key key = new Key(currentRowIndex, casTag, itemName, rig, status, dateOfLog, loggedBy, assignedTo, notes);
			keys.add(key);

			currentRowIndex++;
		}

		logger.info("Loaded " + keys.size() + " keys.");
		return keys;
	}

	/**
	 * Safety method to handle empty cells at the end of rows. Google Sheets API
	 * often omits trailing empty cells.
	 */
	private static String cleanRowData(List<Object> row, int index) {
		if (index < row.size() && row.get(index) != null) {
			return row.get(index).toString();
		}
		return ""; // Return empty string instead of null
	}

	/**
	 * Updates a single key row in the spreadsheet. Uses a background thread to
	 * avoid freezing the UI.
	 */
	public static void queueKeyUpdate(Key key) {
		CompletableFuture.runAsync(() -> {
			try {
				updateKeyRow(key);
			} catch (Exception e) {
				logger.error("Failed to update key: " + key.getItemName(), e);
				Platform.runLater(
						() -> com.clearanglestudios.drive_catalogue.App.showNotification("Failed to save key update!"));
			}
		});
	}

	private static void updateKeyRow(Key key) throws IOException, GeneralSecurityException {
		Sheets service = GoogleTools.getSheetsService();

		// Calculate the Range: Columns D to H for the specific Row
		// D=Status, E=Date, F=LoggedBy, G=AssignedTo, H=Notes
		String range = SHEET_NAME + "!D" + key.getRowIndex() + ":H" + key.getRowIndex();

		List<Object> rowData = new ArrayList<>();
		rowData.add(key.getStatus()); // Col D
		rowData.add(key.getDateOfLog()); // Col E
		rowData.add(key.getLoggedBy()); // Col F
		rowData.add(key.getAssignedTo()); // Col G
		rowData.add(key.getNotes()); // Col H

		ValueRange body = new ValueRange().setValues(List.of(rowData));

		service.spreadsheets().values().update(SPREADSHEET_ID, range, body).setValueInputOption("RAW").execute();

		logger.info("Successfully updated Key Row: " + key.getRowIndex());
	}
	
	/**
     * Handles the full return process:
     * 1. Archives current state to History.
     * 2. Clears the row in the active sheet.
     * 3. Updates the local Java object.
     */
    public static void queueKeyReturn(Key key) {
        CompletableFuture.runAsync(() -> {
            try {
                archiveToHistory(key);
                clearKeyRow(key);

                key.setStatus("In");
                key.setAssignedTo("");
                key.setLoggedBy(""); 
                key.setNotes("");
                key.setDateOfLog(GoogleTools.getTodaysDate());
                
                logger.info("Key Return Complete: " + key.getItemName());

            } catch (Exception e) {
                logger.error("Failed to return key: " + key.getItemName(), e);
                Platform.runLater(() -> com.clearanglestudios.drive_catalogue.App.showNotification("Failed to process return!"));
            }
        });
    }

    private static void archiveToHistory(Key key) throws IOException, GeneralSecurityException {
        Sheets service = GoogleTools.getSheetsService();
        String range = HISTORY_SHEET_NAME + "!A:E"; 

        List<Object> rowData = new ArrayList<>();
        rowData.add(key.getCasTag());
        rowData.add(key.getItemName());
        rowData.add(key.getDateOfLog()); 
        rowData.add(key.getLoggedBy());  
        rowData.add(key.getAssignedTo());

        ValueRange body = new ValueRange().setValues(List.of(rowData));

        service.spreadsheets().values()
                .append(SPREADSHEET_ID, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    private static void clearKeyRow(Key key) throws IOException, GeneralSecurityException {
        Sheets service = GoogleTools.getSheetsService();
        String range = SHEET_NAME + "!D" + key.getRowIndex() + ":H" + key.getRowIndex();
        
        List<Object> rowData = new ArrayList<>();
        rowData.add("In");                          
        rowData.add(GoogleTools.getTodaysDate());  
        rowData.add("");                            
        rowData.add("");                           
        rowData.add("");                            

        ValueRange body = new ValueRange().setValues(List.of(rowData));

        service.spreadsheets().values()
                .update(SPREADSHEET_ID, range, body)
                .setValueInputOption("RAW")
                .execute();
    }
}
