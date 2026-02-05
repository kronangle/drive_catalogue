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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class DriveReturnController {

//	=====================================================================================
//	
//										DECLARATIONS
//	
//	=====================================================================================

//	User input fields and controls
	@FXML
	private Label loggedInLabel_DriveRetrun;
	@FXML
	private ComboBox<String> driveComboBox_DriveReturn;

//	-------------------------------------------------------------------------------------

//	ComboBox filtering tools
	private ObservableList<String> obserableDriveList = FXCollections.observableArrayList();
	private String oldFilter = "";

//	-------------------------------------------------------------------------------------

//	Initialize Logger
	private static final Logger logger = LogManager.getLogger(DriveReturnController.class);

//	-------------------------------------------------------------------------------------

//	Data Management
	private final IDataService dataService = App.getDataService();

//	-------------------------------------------------------------------------------------

//	Map Keys
	private static final String DRIVE_NAME_KEY = "DriveName";

//	Statuses
	private static final String STATUS_TO_FIND_01 = "out";
	private static final String STATUS_TO_FIND_02 = "ingesting";
	private static final String STATUS_TO_APPLY = "in";

//	Default details for returned drives
	private static final String DETAILS_TO_USE = "Safe'd";

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
		logger.info("Initialising DriveReturnController");
		if (Settings.isShowAllReturn()) {
			logger.info("Admin Mode: Fetching ALL drives for Return...");
			obserableDriveList.addAll(getUnfilteredDriveList());
			driveComboBox_DriveReturn.getItems().addAll(obserableDriveList);
		} else {
			logger.info("Standard Mode: Fetching 'Out' & 'Ingesting' drives only...");
			obserableDriveList.addAll(getFilteredDriveList());
			driveComboBox_DriveReturn.getItems().addAll(obserableDriveList);
		}

//		-------------------------------------------------
//		Set current user's name on label
		loggedInLabel_DriveRetrun.setText(
				loggedInLabelSpacing + loggedInLabelText + dataService.getCurrentUserName() + loggedInLabelSpacing);
//		-------------------------------------------------
//		Add a listener to filter items based on input to combo box
		driveComboBox_DriveReturn.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			filterItems(driveComboBox_DriveReturn, obserableDriveList, newValue);
		});
//		-------------------------------------------------
		logger.info("COMPLETED Initialising DriveReturnController");
	}

//	Returns list of options for combo box
	private List<String> getFilteredDriveList() {

		List<String> tempDriveList = new ArrayList<String>();
		try {
			tempDriveList = dataService.getFilteredDriveNames(STATUS_TO_FIND_01);
			tempDriveList.addAll(dataService.getFilteredDriveNames(STATUS_TO_FIND_02));
			Collections.sort(tempDriveList);
			return tempDriveList;
		} catch (GeneralSecurityException | IOException e) {
			logger.error("Failed to load drive data", e);
			App.showNotification("Error loading drives.");
		}

		return tempDriveList;
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

//	=====================================================================================
//	
//									DATA MANAGEMENT METHODS
//	
//	=====================================================================================

//	Returns the values from the form
	private String[] gatherInfoFromFields() {
		logger.info("Gathering Info from driveReturn");
		String drive = driveComboBox_DriveReturn.getValue();
		String status = STATUS_TO_APPLY;
		String crew = "";
		String details = DETAILS_TO_USE;

		String[] info = { drive, status, crew, details };

		return info;
	}

//	Reset the fields on the form
	private void resetForm() {
		driveComboBox_DriveReturn.setValue(null);
	}

//	Method to filter items in the ComboBox
	private void filterItems(ComboBox<String> comboBox, ObservableList<String> originalDrives, String filter) {
		ObservableList<String> filteredDrives = FXCollections.observableArrayList();
		String trimmedFilter = filter.trim();

		if (!trimmedFilter.isBlank()) {
			if (!trimmedFilter.equals(oldFilter)) {
				for (String drive : originalDrives) {
					if (drive.toLowerCase().contains(trimmedFilter.toLowerCase())) {
						filteredDrives.add(drive);
					}
				}
				oldFilter = trimmedFilter;
			}
			comboBox.setItems(filteredDrives);
		} else {
			comboBox.setItems(obserableDriveList);
		}
	}

//	=====================================================================================
//	
//									JAVAFX METHODS
//	
//	=====================================================================================

//	Submits the form
//	@FXML
//	private void returnButtonClickedOld() {
//		App.showNotification("Processing...");
//		logger.info("Processing data from returnButtonClicked");
////		Add a short delay before switching the pane
//		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
//		delay.setOnFinished(event -> {
//			try {
//				String[] info = gatherInfoFromFields();
//
//				if (info != null) {
////					Get results from verifying the info package
//					Map<String, Boolean> verifyResults = GoogleTools.verifyInfo(info);
//
////					Execute form submission if no false values
//					if (!verifyResults.values().contains(false)) {
//						GoogleTools.pushChangesToSheet(info);
//						logger.info("COMPLETED PROCESSING INFO");
//						resetForm();
//						JavaFXTools.loadScene(FxmlView.HOME);
//					} else {
////						The final error message to show
//						StringBuilder notification = new StringBuilder();
////						Check fields that were verified, if false Highlight and return error message
//						JavaFXTools.verifyField(verifyResults, DRIVE_NAME_KEY, driveComboBox_DriveReturn, notification);
////						Show and log error if it exists
//						if (!notification.isEmpty()) {
//							logger.warn(String.format(("Invalid input detected:\n" + notification)));
//							App.showNotification("Invalid input detected:\n" + notification.toString());
//						}
//					}
//
//				} else {
//					logger.warn("info is null");
//					App.showNotification("There is no info to push to spreadsheet");
//				}
//			} catch (IOException e) {
//				logger.warn("Failed to push changes to the spreadsheet");
//				logger.error("IOException - ", e);
//				App.showNotification("Failed to push changes to the spreadsheet - " + e.getMessage());
//			} catch (GeneralSecurityException e) {
//				logger.warn("Failed to push changes to the spreadsheet");
//				logger.error("GeneralSecurityException - ", e);
//				App.showNotification("Failed to push changes to the spreadsheet - " + e.getMessage());
//			}
//		});
//		delay.play();
//	}

	@FXML
	private void returnButtonClicked() {
		logger.info("Processing data from returnButtonClicked");

		String[] info = gatherInfoFromFields();

		if (info == null) {
			logger.warn("info is null");
			App.showNotification("There is no info to save");
			return;
		}

		try {
			Map<String, Boolean> verifyResults = dataService.verifyInfo(info);

			if (verifyResults.values().contains(false)) {
				StringBuilder notification = new StringBuilder();
				// Highlight the UI fields
				JavaFXTools.verifyField(verifyResults, DRIVE_NAME_KEY, driveComboBox_DriveReturn, notification);

				if (!notification.isEmpty()) {
					logger.warn("Invalid input detected:\n" + notification);
					App.showNotification("Invalid input detected:\n" + notification.toString());
				}
				return; // Don't queue invalid data
			}

			SheetUpdate updateTask = new SheetUpdate(info);
			dataService.queueSheetUpdate(updateTask);

			logger.info("Update queued successfully. Navigating home.");
			App.showNotification("Saving in background...");

			resetForm();
			// JavaFXTools.loadScene(FxmlView.HOME);
			App.showNotification("Drive queued! Ready for next entry.");
			logger.info("Task queued. Form reset for next entry.");

		} catch (IOException | GeneralSecurityException e) {
			logger.warn("Validation check failed");
			logger.error("Exception during validation", e);
			App.showNotification("Error checking data - " + e.getMessage());
		}
	}

//	Cancels the form
	@FXML
	private void cancelButtonClicked() {
		resetForm();
		JavaFXTools.loadScene(FxmlView.HOME);
	}

}
