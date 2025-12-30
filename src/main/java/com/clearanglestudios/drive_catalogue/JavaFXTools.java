package com.clearanglestudios.drive_catalogue;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javafx.animation.PauseTransition;
import javafx.scene.control.Control;
import javafx.util.Duration;

public class JavaFXTools {
	
//	=====================================================================================
//	
//										DECLARATIONS
//	
//	=====================================================================================

//	Initialize Logger
	private static final Logger logger = LogManager.getLogger(JavaFXTools.class);
	
//	-------------------------------------------------------------------------------------

//	Map Keys
	private static final String DRIVE_NAME_KEY = "DriveName";
	private static final String PC_NAME_KEY = "PcName";
	private static final String CREW_NAME_KEY = "CrewName";
	private static final String DETAILS_KEY = "Details";
	
//	Individual Error Messages
	private static final String DRIVE_ERROR_MESSAGE = "Drive Name not found.\n   *Please choose a name\n     from the list.";
	private static final String PC_ERROR_MESSAGE = "PC Name not found.\n   *Please choose a name\n     from the list.";
	private static final String CREW_ERROR_MESSAGE = "Crew Name not found.\n   *Please choose a name\n     from the list.";
	private static final String DETAILS_ERROR_MESSAGE = "Details are empty.\n   *Please fill in a\n     Production or Other reason.";
	
//	Unknown Error Message
	private static final String UNKNOWN_ERROR_MESSAGE = "Unknown error was encountered.\n   *Please review your inputs.\n   *Please ensure you are logged into your Google Account.\n   *Please contact Alex Kron if the error persists.";
	
//	Map of all error messages attached to keys
	private static final Map<String, String> ERROR_MESSAGES = Map.ofEntries(
			Map.entry(DRIVE_NAME_KEY, DRIVE_ERROR_MESSAGE), 
			Map.entry(PC_NAME_KEY, PC_ERROR_MESSAGE),
			Map.entry(CREW_NAME_KEY, CREW_ERROR_MESSAGE), 
			Map.entry(DETAILS_KEY, DETAILS_ERROR_MESSAGE));

//	=====================================================================================
//	
//									SHARED METHODS
//	
//	=====================================================================================
	
//	If a field holds an invalid value on submission, it will be highlighted and logged
	public static void verifyField(Map<String, Boolean> verifyResults, String key, Control control,
			StringBuilder notification) {
		if (!verifyResults.getOrDefault(key, false)) {
			highlightInvalidInput(control);
			logger.warn(String.format("Invalid input: %s", ERROR_MESSAGES.getOrDefault(key, UNKNOWN_ERROR_MESSAGE)));
			notification.append("-> ").append(ERROR_MESSAGES.getOrDefault(key, UNKNOWN_ERROR_MESSAGE)).append("\n");
		}
	}

//	Highlights the given control with a red border
	public static void highlightInvalidInput(Control control) {
//		Highlight with red border
		control.setStyle("-fx-border-color: red; -fx-border-width: 1;");
//		Reset to normal when selected
		control.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) { // TextField gets focus
				control.setStyle(""); // Reset CSS
			}
		});
	}
	
//	Reset back to primary pane
	public static void switchToHome() throws IOException {
		App.showNotification("Loading Home Page...");
		logger.info("Switching back to home pane");
//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
				App.setRoot("Primary");
			} catch (IOException e) {
				logIOExceptionFromHomePage(e.getCause().toString(), e);
			}
		});
		delay.play();
	}

//	=====================================================================================
//	
//									ERROR METHODS
//	
//	=====================================================================================
	
//	Logs exceptions for switchToHome method
	public static void logIOExceptionFromHomePage(String clue, Exception e) {
		logger.warn(String.format("Failed to load Home Page - %s", clue));
		logger.error("IOException - ", e);
		App.showNotification(String.format("Failed to load Home Page - %s", clue));
	}

}
