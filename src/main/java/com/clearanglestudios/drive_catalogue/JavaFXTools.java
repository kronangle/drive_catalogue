package com.clearanglestudios.drive_catalogue;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.util.Duration;

public class JavaFXTools {

	// =====================================================================================
	// CONSTANTS
	// =====================================================================================

	private static final Logger logger = LogManager.getLogger(JavaFXTools.class);

	// Map Keys
	private static final String DRIVE_NAME_KEY = "DriveName";
	private static final String PC_NAME_KEY = "PcName";
	private static final String CREW_NAME_KEY = "CrewName";
	private static final String DETAILS_KEY = "Details";

	// Error Messages
	private static final Map<String, String> ERROR_MESSAGES = Map.ofEntries(
			Map.entry(DRIVE_NAME_KEY, "Drive Name not found.\n   *Please choose a name\n     from the list."),
			Map.entry(PC_NAME_KEY, "PC Name not found.\n   *Please choose a name\n     from the list."),
			Map.entry(CREW_NAME_KEY, "Crew Name not found.\n   *Please choose a name\n     from the list."),
			Map.entry(DETAILS_KEY, "Details are empty.\n   *Please fill in a\n     Production or Other reason."));

	private static final String UNKNOWN_ERROR_MESSAGE = "Unknown error encountered.";

	// =====================================================================================
	//
	// NAVIGATION METHODS
	//
	// =====================================================================================

//	Handles all scene transitions between FXML views
	public static void loadScene(FxmlView view) {
		String fxmlName = view.getFxmlName();

		App.hideNotification();
		App.showNotification("Loading " + view + "...");
		logger.info("Switching scene to: " + view + " " + fxmlName);

		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
				setRoot(fxmlName);
			} catch (IOException e) {
				logIOExceptionFromHomePage(e.getCause().toString(), e);
			}
		});
		delay.play();
	}

//	Used to switch the GUI's active scene
	private static void setRoot(String fxml) throws IOException {
		Parent newRoot = loadFXML(fxml);

		String cssPath = App.class.getResource("styles.css").toExternalForm();
		newRoot.getStylesheets().add(cssPath);

		App.getNotificationPane().setContent(newRoot);
		App.hideNotification();
	}

//  Used to load the FXML file for new scene
	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	// =====================================================================================
	//
	// VERIFICATION METHODS
	//
	// =====================================================================================

	public static void verifyField(Map<String, Boolean> verifyResults, String key, Control control,
			StringBuilder notification) {
		if (!verifyResults.getOrDefault(key, false)) {
			highlightInvalidInput(control);
			String error = ERROR_MESSAGES.getOrDefault(key, UNKNOWN_ERROR_MESSAGE);
			logger.warn("Invalid input: " + error);
			notification.append("-> ").append(error).append("\n");
		}
	}

	public static void highlightInvalidInput(Control control) {
		control.setStyle("-fx-border-color: red; -fx-border-width: 1;");
		control.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				control.setStyle(""); // Reset CSS on focus
			}
		});
	}

	public static void logIOExceptionFromHomePage(String clue, Exception e) {
		logger.warn(String.format("Failed to load Home Page - %s", clue));
		logger.error("IOException - ", e);
	}
}