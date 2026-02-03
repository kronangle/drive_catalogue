package com.clearanglestudios.drive_catalogue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.GoogleTools;
import com.clearanglestudios.objects.Crew;
import com.clearanglestudios.objects.Drive;
import com.clearanglestudios.objects.SheetUpdate;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class DriveAssignController {

//	===================================================================
//	
//						DECLARATIONS
//	
//	===================================================================

//	User input fields and controls
	@FXML
	private ChoiceBox<String> sizeChoiceBox_DriveAssign; // Filter for which drives to display
	@FXML
	private ChoiceBox<String> departmentChoiceBox_DriveAssign; // Filter for which crew to display
	@FXML
	private TextField productionTextField_DriveAssign; // New value for assignment
	@FXML
	private TextField otherTextField_DriveAssign; // New value for assignment
	@FXML
	private RadioButton productionRadioButton_DriveAssign; // Select which textField to use
	@FXML
	private RadioButton otherRadioButton_DriveAssign; // Select which textField to use.
	@FXML
	private Label loggedInLabel_DriveAssign; // To show the user that they are logged in.
	@FXML
	private ComboBox<String> driveComboBox_DriveAssign; // Actual available drives
	@FXML
	private ComboBox<String> crewComboBox_DriveAssign; // Actual available crew

//	----------------------------------------------------------------

//	ComboBox filtering tools
	private ObservableList<String> obserableDriveList = FXCollections.observableArrayList();
	private String oldDriveFilter = "";
	private ObservableList<String> obserableCrewList = FXCollections.observableArrayList();
	private String oldCrewFilter = "";
	private boolean isUpdatingDrives = false;
	private boolean isUpdatingCrew = false;

//	----------------------------------------------------------------

//	Google spreadsheet data objects
	private ArrayList<Drive> drives = new ArrayList<>();
	private ArrayList<Crew> crewList = new ArrayList<>();

//	----------------------------------------------------------------

//	Initialize Logger
	private static final Logger logger = LogManager.getLogger(DriveAssignController.class);

//	----------------------------------------------------------------

//	Map Keys
	private static final String DRIVE_NAME_KEY = "DriveName";
	private static final String CREW_NAME_KEY = "CrewName";
	private static final String DETAILS_KEY = "Details";

//	Statuses
	private static final String STATUS_TO_APPLY = "out";
	private static final String STATUS_TO_FIND = "in";

//	Option controls for choice and combo boxes
	private static final String DEFAULT = "All";

//	----------------------------------------------------------------

//	Logged in user label
	private static final String loggedInLabelText = "Logged in as: ";
	private static final String loggedInLabelSpacing = "  ";


//	===================================================================
//	
//						START UP
//	
//	===================================================================

//	Runs before displaying the pane
	@FXML
	public void initialize() {
//		-------------------------------------------------
		logger.info("Initialising DriveIngestController");
//		App.hideNotification();
//		-------------------------------------------------
//		Set current user's name on label
		loggedInLabel_DriveAssign.setText(loggedInLabelSpacing + loggedInLabelText
				+ GoogleTools.getCurrentUserName() + loggedInLabelSpacing);
//		-------------------------------------------------
//		Try get Drive data from the spreadsheet
		try {
			drives = GoogleTools.getDriveData();
		} catch (GeneralSecurityException e) {
			GoogleTools.logGeneralSecurityException("Drive", e);
		} catch (IOException e) {
			GoogleTools.logIOException("Drive", e);
		}
//		-------------------------------------------------
//		Try get Crew data from the spreadsheet
		try {
			crewList = GoogleTools.getCrewData();
		} catch (GeneralSecurityException e) {
			GoogleTools.logGeneralSecurityException("Crew", e);
		} catch (IOException e) {
			GoogleTools.logIOException("Crew", e);
		}
//		-------------------------------------------------
//		Get and Set options in choice boxes
		try {
			obserableDriveList.addAll(GoogleTools.getFilteredDriveNames("in"));
			driveComboBox_DriveAssign.getItems().addAll(obserableDriveList);
		} catch (GeneralSecurityException e) {
			GoogleTools.logGeneralSecurityException("Filtered Drive", e);
		} catch (IOException e) {
			GoogleTools.logIOException("Filtered Drive", e);
		}
//		-------------------------------------------------
//		Setup form
		setupChoiceBoxes();
		setupComboBoxes();
//		-------------------------------------------------
		
		
		logger.info("COMPLETED Initialising DriveIngestController");
	}

//	Adds the options and listeners to the choice boxes
	private void setupChoiceBoxes() {
//		Populate choice box options
		sizeChoiceBox_DriveAssign.getItems().addAll(getListOfSizes());
		departmentChoiceBox_DriveAssign.getItems().addAll(getListOfDepartments());
//		-------------------------------------------------
//		Set default value for filters
		sizeChoiceBox_DriveAssign.setValue(DEFAULT);
		departmentChoiceBox_DriveAssign.setValue(DEFAULT);
// 		-------------------------------------------------
// 		Add listeners to filters
		sizeChoiceBox_DriveAssign.setOnAction(event -> {
			sizeSelected();
		});
		departmentChoiceBox_DriveAssign.setOnAction(event -> {
			departmentSelected();
		});
	}

//	Adds the options and listeners to the combo boxes
	private void setupComboBoxes() {
//		Populate combo box options
		obserableCrewList.addAll(getListOfCrewNames());
		crewComboBox_DriveAssign.getItems().addAll(obserableCrewList);
//		-------------------------------------------------
//		Add a listener to filter items based on input
		driveComboBox_DriveAssign.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			filterDrives(driveComboBox_DriveAssign, obserableDriveList, newValue);
		});
		crewComboBox_DriveAssign.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			filterCrew(crewComboBox_DriveAssign, obserableCrewList, newValue);
		});
	}

//	===================================================================
//	
//					DATA MANAGEMENT METHODS
//	
//	===================================================================

//	Returns a list of unique department names
	private ArrayList<String> getListOfDepartments() {
		Set<String> departments = new HashSet<>();
		departments.add(DEFAULT); // Default value
//		Department list
		for (Crew crew : crewList) {
			departments.add(crew.getDepartment());
		}
		ArrayList<String> departmentList = new ArrayList<>(departments);
		Collections.sort(departmentList);

		return departmentList;
	}

//	Returns a list of unique drive sizes
	private ArrayList<String> getListOfSizes() {
		Set<String> sizes = new HashSet<>();
		sizes.add(DEFAULT); // Default value
//		Drive size list
		for (Drive drive : drives) {
			sizes.add(drive.getSize());
		}
		ArrayList<String> sizesSorted = new ArrayList<>(sizes);
		Collections.sort(sizesSorted);

		return sizesSorted;
	}

//	Returns a list of crew names
	private ArrayList<String> getListOfCrewNames() {
		ArrayList<String> crewNames = new ArrayList<>();
//		Crew list of names
		for (Crew crew : crewList) {
			crewNames.add(crew.getName().trim());
		}
		Collections.sort(crewNames);

		return crewNames;
	}

//	Returns a list of crew names, filtered by department
	private ArrayList<String> getListOfCrewNames(String filter) {
		ArrayList<String> crewNames = new ArrayList<>();

//		Crew list of names
		for (Crew crew : crewList) {
			if (filter.toLowerCase().trim().equalsIgnoreCase(DEFAULT)) {
				crewNames.add(crew.getName().trim());
			} else {
				if (crew.getDepartment().toLowerCase().trim().equals(filter.toLowerCase())) {
					crewNames.add(crew.getName().trim());
				}
			}

		}
		Collections.sort(crewNames);

		return crewNames;
	}

//	Returns values from form fields
	private String[] gatherInfoFromFields() {
		logger.info("Gathering Info");
		String drive = driveComboBox_DriveAssign.getValue();
		String crew = crewComboBox_DriveAssign.getValue();
		String details = (!productionTextField_DriveAssign.isDisabled()) ? productionTextField_DriveAssign.getText()
				: otherTextField_DriveAssign.getText();
		String status = STATUS_TO_APPLY;
		String[] info = { drive, status, crew, details };

		return info;

	}

//	----------------------------------------------------------------

//	Logic for filtering by size
	private void sizeSelected() {
		isUpdatingDrives = true;
		driveComboBox_DriveAssign.getItems().clear();
		obserableDriveList.clear();
		try {
			obserableDriveList
					.addAll(GoogleTools.getFilteredDriveNames(STATUS_TO_FIND, sizeChoiceBox_DriveAssign.getValue()));
			driveComboBox_DriveAssign.getItems().addAll(obserableDriveList);
		} catch (GeneralSecurityException e) {
			GoogleTools.logGeneralSecurityException("Size - Filtered Drive", e);
		} catch (IOException e) {
			GoogleTools.logIOException("Size - Filtered Drive", e);
		} finally {
			isUpdatingDrives = false;
		}

	}

//	Logic for filtering by department
	private void departmentSelected() {
		isUpdatingCrew = true;
		crewComboBox_DriveAssign.getItems().clear();
		obserableCrewList.clear();
		obserableCrewList.addAll(getListOfCrewNames(departmentChoiceBox_DriveAssign.getValue()));
		crewComboBox_DriveAssign.getItems().addAll(obserableCrewList);
		isUpdatingCrew = false;
	}

//	Logic for drive combo box live filtering
	private void filterDrives(ComboBox<String> comboBox, ObservableList<String> originalDrives, String filter) {
		if (!isUpdatingDrives) {

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

	}

//	Logic for crew combo box live filtering
	private void filterCrew(ComboBox<String> comboBox, ObservableList<String> originalCrew, String filter) {
		if (!isUpdatingCrew) {

			ObservableList<String> filteredCrew = FXCollections.observableArrayList();
			String trimmedFilter = filter.trim();

			if (!trimmedFilter.isBlank()) {
				if (!trimmedFilter.equals(oldCrewFilter)) {
					for (String drive : originalCrew) {
						if (drive.toLowerCase().contains(trimmedFilter.toLowerCase())) {
							filteredCrew.add(drive);
						}
					}
					oldCrewFilter = trimmedFilter;
				}
				comboBox.setItems(filteredCrew);
			} else {
				comboBox.setItems(obserableCrewList);
			}
		}

	}

//	----------------------------------------------------------------

//	Returns active the text field object
	private TextField getActiveTextField() {
		return (!productionTextField_DriveAssign.isDisable()) ? productionTextField_DriveAssign
				: otherTextField_DriveAssign;
	}

//	Reset the fields on the form
	private void resetForm() {
		productionTextField_DriveAssign.setText("");
		otherTextField_DriveAssign.setText("");
		sizeChoiceBox_DriveAssign.setValue(DEFAULT);
		departmentChoiceBox_DriveAssign.setValue(DEFAULT);
		driveComboBox_DriveAssign.setValue("");
//		crewComboBox_DriveAssign.setValue("");
	}

//	===================================================================
//	
//					JAVAFX METHODS
//	
//	===================================================================

//	Submits the form
	@FXML
	private void assignButtonClicked() {
		logger.info("Processing data from assignButtonClicked");

		String[] info = gatherInfoFromFields();

		if (info == null) {
			logger.warn("info is null");
			App.showNotification("There is no info to push to spreadsheet");
			return;
		}

		try {
			// VERIFY DATA 
			Map<String, Boolean> verifyResults = GoogleTools.verifyInfo(info);

			// CHECK FOR ERRORS
			if (verifyResults.values().contains(false)) {
				StringBuilder notification = new StringBuilder();

				JavaFXTools.verifyField(verifyResults, DRIVE_NAME_KEY, driveComboBox_DriveAssign, notification);
				JavaFXTools.verifyField(verifyResults, CREW_NAME_KEY, crewComboBox_DriveAssign, notification);
				JavaFXTools.verifyField(verifyResults, DETAILS_KEY, getActiveTextField(), notification);

				// Show error
				if (!notification.isEmpty()) {
					logger.warn(String.format(("Invalid input detected:\n" + notification)));
					App.showNotification("Invalid input detected:\n" + notification.toString());
				}
				return; // Do not queue invalid data.
			}

			// Create ticket and send to background thread 
			SheetUpdate ticket = new SheetUpdate(info);
			GoogleTools.queueSheetUpdate(ticket);

			logger.info("Assignment task queued successfully. Form reset for next entry.");

			// Clear the form 
			resetForm();
			App.showNotification("Assignment queued! Ready for next entry.");

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

//	Toggles which textField is active
	@FXML
	private void setTextFieldToUse() {
		if (productionRadioButton_DriveAssign.isSelected()) {
			productionTextField_DriveAssign.setDisable(false);
			otherTextField_DriveAssign.setDisable(true);
			logger.info("Enabling Production textField");
		} else {
			productionTextField_DriveAssign.setDisable(true);
			otherTextField_DriveAssign.setDisable(false);
			logger.info("Enabling Other textField");
		}
	}


}
