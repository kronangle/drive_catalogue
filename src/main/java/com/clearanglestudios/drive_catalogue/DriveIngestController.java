package com.clearanglestudios.drive_catalogue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.GoogleTools;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class DriveIngestController {

//	=====================================================================================
//	
//										DECLARATIONS
//	
//	=====================================================================================

//	User input fields and controls
	@FXML
	private ChoiceBox<String> driveChoiceBox_DriveIngest;
	@FXML
	private ChoiceBox<String> pcChoiceBox_DriveIngest;
	@FXML
	private ComboBox<String> driveComboBox_DriveIngest;
	@FXML
	private Label loggedInLabel_DriveIngest;

//	-------------------------------------------------------------------------------------

//	ComboBox filtering tools
	private ObservableList<String> obserableDriveList = FXCollections.observableArrayList();
	private String oldDriveFilter = "";

//	-------------------------------------------------------------------------------------

//	Initialize Logger
	private static final Logger logger = LogManager.getLogger(DriveIngestController.class);

//	-------------------------------------------------------------------------------------

//	Map Keys
	private static final String DRIVE_NAME_KEY = "DriveName";
	private static final String PC_NAME_KEY = "PcName";

//	Statuses
	private static final String STATUS_TO_FIND = "out";
	private static final String STATUS_TO_APPLY = "ingesting";

//	-------------------------------------------------------------------------------------

//	Logged in user label
	private static final String loggedInLabelText = "Logged in as: ";
	private static final String loggedInLabelSpacing = "  ";

//	=====================================================================================
//	
//										START UP
//	
//	=====================================================================================

//	Runs before displaying the pane
	@FXML
	public void initialize() {
//		App.hideNotification();
		logger.info("Initialising DriveIngestController");
//		-----------------------------------------------------
//		Try get the drive data from the spreadsheet
		try {
			obserableDriveList.addAll(GoogleTools.getFilteredDriveNames(STATUS_TO_FIND));
			driveComboBox_DriveIngest.getItems().addAll(obserableDriveList);
		} catch (GeneralSecurityException e) {
			GoogleTools.logGeneralSecurityException("Filtered Drive", e);
		} catch (IOException e) {
			GoogleTools.logIOException("Filtered Drive", e);
		}
//		-----------------------------------------------------
//		Try get the ingest PC names from the spreadsheet
		try {
			pcChoiceBox_DriveIngest.getItems().addAll(GoogleTools.getPcNames());
		} catch (GeneralSecurityException e) {
			GoogleTools.logGeneralSecurityException("PC", e);
		} catch (IOException e) {
			GoogleTools.logIOException("PC", e);
		}
//		-------------------------------------------------
//		Set current user's name on label
		loggedInLabel_DriveIngest.setText(loggedInLabelSpacing + loggedInLabelText
				+ LoginPageController.getCurrentUserName() + loggedInLabelSpacing);
//		-------------------------------------------------
//		Add a listener to filter items based on input
		driveComboBox_DriveIngest.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			filterDrives(driveComboBox_DriveIngest, obserableDriveList, newValue);
		});
//		-------------------------------------------------
		logger.info("COMPLETED Initialising DriveIngestController");
	}

//	=====================================================================================
//	
//									DATA MANAGEMENT METHODS
//	
//	=====================================================================================

//	Method to filter items in the ComboBox
	private void filterDrives(ComboBox<String> comboBox, ObservableList<String> originalDrives, String filter) {
		ObservableList<String> filteredDrives = FXCollections.observableArrayList();
		String trimmedFilter = filter.trim();

		if (!trimmedFilter.isBlank()) {
			if (!trimmedFilter.equals(oldDriveFilter)) {
				for (String drive : originalDrives) {
					if (drive.toLowerCase().contains(trimmedFilter.toLowerCase())) {
						filteredDrives.add(drive);
					}
				}
				oldDriveFilter = trimmedFilter;
			}
			comboBox.setItems(filteredDrives);
		} else {
			comboBox.setItems(obserableDriveList);
		}

	}

//	Returns the values from the form
	private String[] gatherInfoFromFields() {
		logger.info("Gathering info from DriveIngest");

		String drive = driveComboBox_DriveIngest.getValue();
		String status = STATUS_TO_APPLY;
		String details = pcChoiceBox_DriveIngest.getValue();
		String crew = "";

		try {
			crew = GoogleTools.getFilteredCrewName(drive);
		} catch (GeneralSecurityException e) {
			GoogleTools.logGeneralSecurityException("Ingesting - Filtered Crew", e);
		} catch (IOException e) {
			GoogleTools.logIOException("Ingesting - Filtered Crew", e);
		}

		String[] info = { drive, status, crew, details };

		return info;
	}

//	Reset the fields on the form
	private void resetForm() {
		pcChoiceBox_DriveIngest.setValue(null);
	}

//	=====================================================================================
//	
//									JAVAFX METHODS
//	
//	=====================================================================================

//	Submits the form
	@FXML
	private void ingestButtonClicked() {
		App.showNotification("Processing...");
		logger.info("Processing data from ingestButtonClicked");
//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
				String[] info = gatherInfoFromFields();

				if (info != null) {
//					Get results from verifying the info package
					Map<String, Boolean> verifyResults = GoogleTools.verifyInfo(info);

//					Execute form submission if no false values
					if (!verifyResults.values().contains(false)) {
						GoogleTools.pushChangesToSheet(info);
						logger.info("COMPLETED PROCESSING INFO");
						resetForm();
						JavaFXTools.loadScene(FxmlView.HOME);
					} else {
//						The final error message to show
						StringBuilder notification = new StringBuilder();
//	 					Check fields that were verified, if false Highlight and return error message
						JavaFXTools.verifyField(verifyResults, DRIVE_NAME_KEY, driveComboBox_DriveIngest, notification);
						JavaFXTools.verifyField(verifyResults, PC_NAME_KEY, pcChoiceBox_DriveIngest, notification);
//						Show and log error if it exists
						if (!notification.isEmpty()) {
							logger.warn(String.format(("Invalid input detected:\n" + notification)));
							App.showNotification("Invalid input detected:\n" + notification.toString());
						}
					}

				} else {
					logger.warn("info is null");
					App.showNotification("There is no info to push to spreadsheet");
				}

			} catch (IOException e) {
				logger.warn("Failed to push changes to the spreadsheet");
				logger.error("IOException - ", e);
				App.showNotification("Failed to push changes to the spreadsheet - " + e.getMessage());
			} catch (GeneralSecurityException e) {
				logger.warn("Failed to push changes to the spreadsheet");
				logger.error("GeneralSecurityException - ", e);
				App.showNotification("Failed to push changes to the spreadsheet - " + e.getMessage());
			}
		});
		delay.play();
	}

//	Cancels the form
	@FXML
	private void cancelButtonClicked() {
		resetForm();
		JavaFXTools.loadScene(FxmlView.HOME);
	}

}
