package com.clearanglestudios.drive_catalogue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.IDataService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class AdminPanelController {

//	=====================================================================================
//	
//										DECLARATIONS
//	
//	=====================================================================================

//	User input fields and controls
	@FXML
	private Label loggedInLabel_AdminPanel;
	@FXML
	private Button saveButton_AdminPanel;
	@FXML
	private Button cancelButton_AdminPanel;
	@FXML
	private CheckBox allDrives_Ingest;
	@FXML
	private CheckBox allDrives_Return;
	@FXML
	private CheckBox emailToggle;

	// -------------------------------------------------------------------------------------

	// Initialize Logger
	private static final Logger logger = LogManager.getLogger(AdminPanelController.class);

//	-------------------------------------------------------------------------------------

//	Data Management
	private final IDataService dataService = App.getDataService();

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
//		-------------------------------------------------
//		Set current user's name on label
		loggedInLabel_AdminPanel.setText(
				loggedInLabelSpacing + loggedInLabelText + dataService.getCurrentUserName() + loggedInLabelSpacing);
//		-------------------------------------------------
		// Load Settings
		allDrives_Ingest.setSelected(Settings.isShowAllIngest());
		allDrives_Return.setSelected(Settings.isShowAllReturn());
		emailToggle.setSelected(Settings.isEmailEnabled());

//		-------------------------------------------------		
		logger.info("COMPLETED Initialising DriveReturnController");
	}

//	=====================================================================================
//	
//									JAVAFX METHODS
//	
//	=====================================================================================

	@FXML
	private void saveButtonClicked() {
		logger.info("Saving Admin Settings...");

		// Save Settings
		Settings.setShowAllIngest(allDrives_Ingest.isSelected());
		Settings.setShowAllReturn(allDrives_Return.isSelected());
		Settings.setEmailEnabled(emailToggle.isSelected());

		App.showNotification("Settings Saved!");

		JavaFXTools.loadScene(FxmlView.HOME);
	}

//	Cancels the form
	@FXML
	private void cancelButtonClicked() {
		JavaFXTools.loadScene(FxmlView.HOME);
	}

}
