package com.clearanglestudios.drive_catalogue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.IDataService;
import com.clearanglestudios.objects.Crew;
import com.clearanglestudios.objects.CrewDrive;
import com.clearanglestudios.objects.Drive;
import com.clearanglestudios.objects.DriveProperty;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

public class DriveQueryController {

//	=====================================================================================
//	
//										DECLARATIONS
//	
//	=====================================================================================

//	User input fields and controls
	@FXML
	private Button searchDrivesButton_DriveQuery;
	@FXML
	private Button searchNamesButton_DriveQuery;
	@FXML
	private Button finishedButton_DriveQuery;
	@FXML
	private ChoiceBox<String> sizeChoiceBox_DriveQuery;
	@FXML
	private ChoiceBox<String> statusChoiceBox_DriveQuery;
	@FXML
	private ChoiceBox<String> departmentChoiceBox_DriveQuery;
	@FXML
	private TableView<DriveProperty> tableViewDrives_DriveQuery;
	@FXML
	private TableView<CrewDrive> tableViewCrew_DriveQuery;
	@FXML
	private TableColumn<DriveProperty, String> columnPropertyName_DriveQuery;
	@FXML
	private TableColumn<DriveProperty, String> columnPropertyValue_DriveQuery;
	@FXML
	private TableColumn<CrewDrive, String> columnPropertyDriveName_DriveQuery;
	@FXML
	private TableColumn<CrewDrive, String> columnPropertyDriveSize_DriveQuery;
	@FXML
	private TableColumn<CrewDrive, String> columnPropertyDriveStatus_DriveQuery;
	@FXML
	private TableColumn<CrewDrive, String> columnPropertyDriveDetails_DriveQuery;
	@FXML
	private TableColumn<CrewDrive, String> columnPropertyDriveDate_DriveQuery;
	@FXML
	private TableColumn<CrewDrive, String> columnPropertyDriveLogger_DriveQuery;
	@FXML
	private ComboBox<String> crewNameComboBox_DriveQuery;
	@FXML
	private ComboBox<String> driveNameComboBox_DriveQuery;
	@FXML
	private Label loggedInLabel_DriveQuery;

//	-------------------------------------------------------------------------------------

//	ComboBox filtering tools
	private ObservableList<String> obserableDriveList = FXCollections.observableArrayList();
	private String oldDriveFilter = "";
	private ObservableList<String> obserableCrewList = FXCollections.observableArrayList();
	private String oldCrewFilter = "";
	private boolean isUpdatingDrives = false;
	private boolean isUpdatingCrew = false;

//	-------------------------------------------------------------------------------------

//	Google spreadsheet data objects
	private List<Drive> drives = new ArrayList<>();
	private List<Crew> crewList = new ArrayList<>();

//	-------------------------------------------------------------------------------------

//	Initialize Logger
	private static final Logger logger = LogManager.getLogger(DriveQueryController.class);
	
//	-------------------------------------------------------------------------------------
	
//	Data Management
	private final IDataService dataService = App.getDataService();

//	-------------------------------------------------------------------------------------

//	Error Messages
	private static final String CREW_SEARCH_ERROR_MESSAGE = "-> Crew Name not found.\n   *Please select a name from the list.";
	private static final String DRIVE_SEARCH_ERROR_MESSAGE = "-> Drive Name not found.\n   *Please select a name from the list.";

//	Statuses
	private static final String STATUS_TO_FIND_01 = "out";
	private static final String STATUS_TO_FIND_02 = "ingesting";
	private static final String STATUS_TO_FIND_03 = "in";

//	Option controls for choice and combo boxes
	private static final String DEFAULT = "All";

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
		logger.info("Initialising DriveQueryController");
//		App.hideNotification();
//		-------------------------------------------------
//		Set current user's name on label
		loggedInLabel_DriveQuery.setText(loggedInLabelSpacing + loggedInLabelText
				+ dataService.getCurrentUserName() + loggedInLabelSpacing);
//		-------------------------------------------------
//		Try get Drive data from the spreadsheet
		try {
			drives = dataService.getDriveData();
		} catch (GeneralSecurityException e) {
			dataService.logGeneralSecurityException("Drive", e);
		} catch (IOException e) {
			dataService.logIOException("Drive", e);
		}
//		-------------------------------------------------
//		Try get Crew data from the spreadsheet
		try {
			crewList = dataService.getCrewData();
		} catch (GeneralSecurityException e) {
			dataService.logGeneralSecurityException("Crew", e);
		} catch (IOException e) {
			dataService.logIOException("Crew", e);
		}
//		-------------------------------------------------
//		Set options in choice boxes
		try {
			obserableDriveList.addAll(getDriveList());
			driveNameComboBox_DriveQuery.getItems().addAll(obserableDriveList);
		} catch (GeneralSecurityException e) {
			dataService.logGeneralSecurityException("Filtered Drive", e);
		} catch (IOException e) {
			dataService.logIOException("Filtered Drive", e);
		}
//		-------------------------------------------------
//		Setup query controls
		setupChoiceBoxes();
		setupComboBoxes();
		setupTableColumns();
//		-------------------------------------------------
//		Set tables' starting visibility
		tableViewCrew_DriveQuery.setVisible(false);
		tableViewDrives_DriveQuery.setVisible(false);
//		-------------------------------------------------
		logger.info("COMPLETED Initialising DriveQueryController");
	}

//	Binds the table columns to the property values
	private void setupTableColumns() {
//		Initialise columns
//		Drive search
		columnPropertyName_DriveQuery.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
		columnPropertyValue_DriveQuery.setCellValueFactory(new PropertyValueFactory<>("propertyValue"));
//		Crew name search
		columnPropertyDriveName_DriveQuery.setCellValueFactory(new PropertyValueFactory<>("driveName"));
		columnPropertyDriveSize_DriveQuery.setCellValueFactory(new PropertyValueFactory<>("driveSize"));
		columnPropertyDriveStatus_DriveQuery.setCellValueFactory(new PropertyValueFactory<>("driveStatus"));
		columnPropertyDriveDetails_DriveQuery.setCellValueFactory(new PropertyValueFactory<>("driveDetails"));
		columnPropertyDriveDate_DriveQuery.setCellValueFactory(new PropertyValueFactory<>("driveDate"));
		columnPropertyDriveLogger_DriveQuery.setCellValueFactory(new PropertyValueFactory<>("driveLogger"));
	}

//	Adds the options and listeners to the choice boxes
	private void setupChoiceBoxes() {
		sizeChoiceBox_DriveQuery.getItems().addAll(getListOfSizes());
		departmentChoiceBox_DriveQuery.getItems().addAll(getListOfDepartments());
		statusChoiceBox_DriveQuery.getItems().addAll(getListOfStatuses());
//		-------------------------------------------------
//		Set default value for filters
		sizeChoiceBox_DriveQuery.setValue(DEFAULT);
		departmentChoiceBox_DriveQuery.setValue(DEFAULT);
		statusChoiceBox_DriveQuery.setValue(DEFAULT);
//		-------------------------------------------------
//		Add listeners to filters
		sizeChoiceBox_DriveQuery.setOnAction(event -> {
			sizeOrStatusSelected();
		});
		departmentChoiceBox_DriveQuery.setOnAction(event -> {
			departmentSelected();
		});
		statusChoiceBox_DriveQuery.setOnAction(event -> {
			sizeOrStatusSelected();
		});
	}

//	Adds the options and listeners to the combo boxes
	private void setupComboBoxes() {
		obserableCrewList.addAll(getListOfCrewNames());
		crewNameComboBox_DriveQuery.getItems().addAll(obserableCrewList);
//		-------------------------------------------------
//		Add a listener to filter items based on input
		driveNameComboBox_DriveQuery.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			filterDrives(driveNameComboBox_DriveQuery, obserableDriveList, newValue);
		});
		crewNameComboBox_DriveQuery.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			filterCrew(crewNameComboBox_DriveQuery, obserableCrewList, newValue);
		});
	}

//	Returns all the options for the combo box
	private ArrayList<String> getDriveList() throws GeneralSecurityException, IOException {
		ArrayList<String> tempDriveList = new ArrayList<>(dataService.getFilteredDriveNames(STATUS_TO_FIND_01));
		tempDriveList.addAll(dataService.getFilteredDriveNames(STATUS_TO_FIND_02));
		tempDriveList.addAll(dataService.getFilteredDriveNames(STATUS_TO_FIND_03));
		Collections.sort(tempDriveList);
		return tempDriveList;
	}

//	=====================================================================================
//	
//									DATA MANAGEMENT METHODS
//	
//	=====================================================================================

//	Method to filter items in the ComboBox
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

//	Method to filter items in the ComboBox
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

//	Searches drives for a matching crew name, binds the properties of the drives and displays it in the table
	private void searchAndDisplayCrewDrives(String crewName) {
//		Clear the table for fresh results
		tableViewDrives_DriveQuery.getItems().clear();
//      Clear old drive data and get new drive data
		drives.clear();
		drives = refreshDriveData();
//		Declare list of CrewDrive objects to populate
		ObservableList<CrewDrive> crewDrivesObservableList = FXCollections.observableArrayList();
//	    Cycle through and find drives matching the crew name
		for (Drive drive : drives) {
			if (drive.getCrew().equalsIgnoreCase(crewName)) {
//	            Create a CrewDrive object with selected details
				CrewDrive crewDrive = new CrewDrive(drive.getName(), // driveName
						drive.getSize(), // driveSize
						drive.getStatus(), // driveStatus
						drive.getAssignment(), // driveDetails
						drive.getDateOfLastLogged(), // driveDate
						drive.getLoggedByUser() // driveLogger
				);
//	            Add the CrewDrive object to the observable list
				crewDrivesObservableList.add(crewDrive);
			}
		}
//	    Bind the observable list to the TableView
		tableViewCrew_DriveQuery.setItems(crewDrivesObservableList);
	}

//	Searches drives for a matching name, binds the properties of the drive and displays it in the table
	private void searchAndDisplayDrive(String driveName) {
//		Clear the table for fresh results
		tableViewDrives_DriveQuery.getItems().clear();
//      Clear old drive data and get new drive data
		drives.clear();
		drives = refreshDriveData();
//      Cycle through and find the matching drive
		for (Drive drive : drives) {
			if (drive.getName().equalsIgnoreCase(driveName)) {
//            	Create the property-value pairs from the drive data
				ObservableList<DriveProperty> driveProperties = FXCollections.observableArrayList(
						new DriveProperty("Serial Number", drive.getSerialNumber()),
						new DriveProperty("Name", drive.getName()), new DriveProperty("Size", drive.getSize()),
						new DriveProperty("Status", drive.getStatus()),
						new DriveProperty("Assignment", drive.getAssignment()),
						new DriveProperty("Crew", drive.getCrew()),
						new DriveProperty("Last Logged", drive.getDateOfLastLogged()),
						new DriveProperty("Logged By User", drive.getLoggedByUser()),
						new DriveProperty("Drive Tag", drive.getDriveTag()),
						new DriveProperty("Cas Tag", drive.getCasTag()));
//              Reveal the relevant columns
				columnPropertyName_DriveQuery.setVisible(true);
				columnPropertyValue_DriveQuery.setVisible(true);
//              Populate the table with the list of property-value pairs
				tableViewDrives_DriveQuery.setItems(driveProperties);
				return; // Exit after finding the drive
			}
		}
	}

//	Returns a fresh list of drives
	private List<Drive> refreshDriveData() {
		try {
			List<Drive> freshDrives = new ArrayList<>();
			freshDrives = dataService.getDriveData();
			return freshDrives;
		} catch (GeneralSecurityException e) {
			dataService.logGeneralSecurityException("Drive", e);
		} catch (IOException e) {
			dataService.logIOException("Drive", e);
		}
		return null;
	}

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

//	Returns a list of crew names
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

//	Returns a list of statuses
	private ArrayList<String> getListOfStatuses() {
		Set<String> statuses = new HashSet<>();
		statuses.add("All"); // Default value
//		Drive size list
		for (Drive drive : drives) {
			statuses.add(drive.getStatus());
		}
		ArrayList<String> statusesSorted = new ArrayList<>(statuses);
		Collections.sort(statusesSorted);

		return statusesSorted;
	}

//	Filters drives based on size selection
	private void sizeOrStatusSelected() {
		isUpdatingDrives = true;
		driveNameComboBox_DriveQuery.getItems().clear();
		obserableDriveList.clear();
		try {
			obserableDriveList.addAll(dataService.getFilteredDriveNames(statusChoiceBox_DriveQuery.getValue(),
					sizeChoiceBox_DriveQuery.getValue()));
			driveNameComboBox_DriveQuery.getItems().addAll(obserableDriveList);
		} catch (GeneralSecurityException e) {
			dataService.logGeneralSecurityException("Filtered Drive", e);
		} catch (IOException e) {
			dataService.logIOException("Filtered Drive", e);
		} finally {
			isUpdatingDrives = false;
		}
	}

//	Filters crew based on department selection
	private void departmentSelected() {
		isUpdatingCrew = true;
		crewNameComboBox_DriveQuery.getItems().clear();
		obserableCrewList.clear();
		obserableCrewList.addAll(getListOfCrewNames(departmentChoiceBox_DriveQuery.getValue()));
		crewNameComboBox_DriveQuery.getItems().addAll(obserableCrewList);
		isUpdatingCrew = false;
	}

//	=====================================================================================
//	
//									JAVAFX METHODS
//	
//	=====================================================================================

//	Executes the Drive search function
	@FXML
	private void searchDrivesButtonClicked() {
		App.showNotification("Searching...");
		logger.info("Searching data from searchDrivesButtonClicked");

//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
//				Get name to search for
				String driveName = driveNameComboBox_DriveQuery.getValue();
//				Get result from verifying the name
				boolean foundDriveName = dataService.doesDriveNameExist(driveName);

				if (foundDriveName) {
					searchAndDisplayDrive(driveName);
					showDrivePropertiesTable();
					logger.info("COMPLETED PROCESSING DRIVE SEARCH");
					driveNameComboBox_DriveQuery.setValue(null);
				} else {
					JavaFXTools.highlightInvalidInput(driveNameComboBox_DriveQuery);
//					Show and log error
					logger.warn(String.format(("Invalid input detected:\n" + DRIVE_SEARCH_ERROR_MESSAGE)));
					App.showNotification("Invalid input detected:\n" + DRIVE_SEARCH_ERROR_MESSAGE);
				}
			} catch (IOException e) {
				dataService.logIOException("searchDrivesButtonClicked", e);
			} catch (GeneralSecurityException e) {
				dataService.logGeneralSecurityException("searchDrivesButtonClicked", e);
			}
		});
		delay.play();
	}

//	Execute the Crew search function
	@FXML
	private void searchNamesButtonClicked() throws GeneralSecurityException, IOException {
		App.showNotification("Searching...");
		logger.info("Searching data from searchNamesButtonClicked");

//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
//				Get name to search for
				String crewName = crewNameComboBox_DriveQuery.getValue();
//				Get result from verifying the name
				boolean foundCrewName = dataService.doesCrewNameExist(crewName);
//				Execute form submission if no false value
				if (foundCrewName) {
					searchAndDisplayCrewDrives(crewName);
					showCrewDriveTable();
					logger.info("COMPLETED PROCESSING CREW SEARCH");
					crewNameComboBox_DriveQuery.setValue(null);
				} else {
					JavaFXTools.highlightInvalidInput(crewNameComboBox_DriveQuery);
//					Show and log error
					logger.warn(String.format(("Invalid input detected:\n" + CREW_SEARCH_ERROR_MESSAGE)));
					App.showNotification("Invalid input detected:\n" + CREW_SEARCH_ERROR_MESSAGE);
				}
			} catch (IOException e) {
				dataService.logIOException("searchCrewButtonClicked", e);
			} catch (GeneralSecurityException e) {
				dataService.logGeneralSecurityException("searchCrewButtonClicked", e);
			}
		});
		delay.play();
	}

	@FXML
	private void finishedButtonClicked() {
		JavaFXTools.loadScene(FxmlView.HOME);
	}

//	Toggles the visibility of the drive properties table
	private void showDrivePropertiesTable() {
		tableViewDrives_DriveQuery.setVisible(true);
		tableViewCrew_DriveQuery.setVisible(false);
		App.hideNotification();
	}

//	Toggles the visibility of the crew drives table
	private void showCrewDriveTable() {
		tableViewDrives_DriveQuery.setVisible(false);
		tableViewCrew_DriveQuery.setVisible(true);
		App.hideNotification();
	}

}
