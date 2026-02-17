package com.clearanglestudios.drive_catalogue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.IDataService;
import com.clearanglestudios.objects.Drive;
import com.clearanglestudios.objects.SheetUpdate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

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

//	Data Management
	private final IDataService dataService = App.getDataService();

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
		logger.info("Initialising DriveIngestController");
//		-----------------------------------------------------
//		Try get the drive data from the spreadsheet
		if (Settings.isShowAllIngest()) {
			logger.info("Admin Mode: Fetching ALL drives for Return...");
			obserableDriveList.addAll(getUnfilteredDriveList());
			driveComboBox_DriveIngest.getItems().addAll(obserableDriveList);
		} else {
			logger.info("Standard Mode: Fetching 'Out' & 'Ingesting' drives only...");
			obserableDriveList.addAll(getFilteredDriveList());
			driveComboBox_DriveIngest.getItems().addAll(obserableDriveList);
		}

//		-----------------------------------------------------
//		Try get the ingest PC names from the spreadsheet
		try {
			pcChoiceBox_DriveIngest.getItems().addAll(dataService.getPcNames());
		} catch (GeneralSecurityException e) {
			dataService.logGeneralSecurityException("PC", e);
		} catch (IOException e) {
			dataService.logIOException("PC", e);
		}
//		-------------------------------------------------
//		Set current user's name on label
		loggedInLabel_DriveIngest.setText(
				loggedInLabelSpacing + loggedInLabelText + dataService.getCurrentUserName() + loggedInLabelSpacing);
//		-------------------------------------------------
//		Add a listener to filter items based on input
		driveComboBox_DriveIngest.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			filterDrives(driveComboBox_DriveIngest, obserableDriveList, newValue);
		});
//		-------------------------------------------------
		logger.info("COMPLETED Initialising DriveIngestController");
	}

//	Returns all the drives names as a list of strings
	private List<String> getUnfilteredDriveList() {
		List<Drive> allDrives = new ArrayList<Drive>();
		List<String> driveNames = new ArrayList<String>();

		try {
			allDrives = dataService.getDriveData();
			for (Drive d : allDrives) {
				driveNames.add(d.getName());
			}
		} catch (Exception e) {
			logger.error("Failed to load drive data", e);
			App.showNotification("Error loading drives.");
		}

		Collections.sort(driveNames);

		return driveNames;
	}

	private List<String> getFilteredDriveList() {
		try {
			return dataService.getFilteredDriveNames(STATUS_TO_FIND);
		} catch (GeneralSecurityException e) {
			dataService.logGeneralSecurityException("PC", e);
		} catch (IOException e) {
			dataService.logIOException("PC", e);
		}
		return new ArrayList<String>();
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
			crew = dataService.getFilteredCrewName(drive);
		} catch (GeneralSecurityException e) {
			dataService.logGeneralSecurityException("Ingesting - Filtered Crew", e);
		} catch (IOException e) {
			dataService.logIOException("Ingesting - Filtered Crew", e);
		}

		String[] info = { drive, status, crew, details };

		return info;
	}

//	Reset the fields on the form
	private void resetForm() {
//		pcChoiceBox_DriveIngest.setValue(null);
		driveComboBox_DriveIngest.setValue(null);
	}

//	=====================================================================================
//	
//									JAVAFX METHODS
//	
//	=====================================================================================

	@FXML
	private void ingestButtonClicked() {
		logger.info("Processing data from ingestButtonClicked");

		String[] info = gatherInfoFromFields();

		if (info == null) {
			logger.warn("info is null");
			App.showNotification("There is no info to push to spreadsheet");
			return;
		}

		try {
			// VERIFY DATA
			Map<String, Boolean> verifyResults = dataService.verifyInfo(info);

			// CHECK FOR ERRORS
			if (verifyResults.values().contains(false)) {
				StringBuilder notification = new StringBuilder();

				// Check fields
				JavaFXTools.verifyField(verifyResults, DRIVE_NAME_KEY, driveComboBox_DriveIngest, notification);
				JavaFXTools.verifyField(verifyResults, PC_NAME_KEY, pcChoiceBox_DriveIngest, notification);

				// Show error
				if (!notification.isEmpty()) {
					logger.warn(String.format(("Invalid input detected:\n" + notification)));
					App.showNotification("Invalid input detected:\n" + notification.toString());
				}
				return; // Do not queue invalid data.
			}

			// Create ticket and send to background thread
			SheetUpdate queueTask = new SheetUpdate(info);
			dataService.queueSheetUpdate(queueTask);

			logger.info("Ingest task queued successfully. Form reset for next entry.");

			// Clear the form
			resetForm();
			App.showNotification("Ingest queued! Ready for next entry.");

		} catch (IOException e) {
			logger.warn("Failed during validation check");
			logger.error("IOException - ", e);
			App.showNotification("Validation Error - " + e.getMessage());
		} catch (GeneralSecurityException e) {
			logger.warn("Failed during validation check");
			logger.error("GeneralSecurityException - ", e);
			App.showNotification("Validation Error - " + e.getMessage());
		}
	}

//	Cancels the form
	@FXML
	private void cancelButtonClicked() {
		resetForm();
		JavaFXTools.loadScene(FxmlView.HOME);
	}

}
