package com.clearanglestudios.drive_catalogue;

import java.io.IOException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.IDataService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class KeyHomeController {

//	=====================================================================================
//	
//										DECLARATIONS
//	
//	=====================================================================================

//	User input fields and controls
	@FXML
	private Button assignButton_KeyHome;
	@FXML
	private Button returnButton_KeyHome;
	@FXML
	private Label loggedInLabel_KeyHome;
	@FXML
	private Label lastSyncLabel_KeyHome;
	@FXML
	private Button refreshButton_KeyHome;
	@FXML
	private Button driveButton_KeyHome;
	@FXML
	private Button logoutButton_KeyHome;

//	-------------------------------------------------------------------------------------

//	Initialize Logger
	private static final Logger logger = LogManager.getLogger(KeyHomeController.class);
	
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
	private void initialize() {
		logger.info("Initialising KeyHome");
//		App.hideNotification();
//		-------------------------------------------------------------------------------------
//		Set the label to the current user's name
		loggedInLabel_KeyHome.setText(loggedInLabelSpacing + loggedInLabelText
				+ dataService.getCurrentUserName() + loggedInLabelSpacing);
//		-------------------------------------------------------------------------------------
		lastSyncLabel_KeyHome.setText("  Last Synced: " + dataService.getLastSyncTime() + "  ");
		logger.info("COMPLETED Initialising KeyHome");
	}


//	=====================================================================================
//	
//									DATA MANAGEMENT
//	
//	=====================================================================================
	
	@FXML
	private void refreshButtonClicked() {
		logger.info("Manual Refresh Triggered from Key Home Page");
//        -------------------------------------
		dataService.clearCache();
        lastSyncLabel_KeyHome.setText("  Last Synced: --:--  ");
	}
	
//	=====================================================================================
//	
//									JAVAFX METHODS
//	
//	=====================================================================================

//	Changes pane to assign pane
	@FXML
	private void switchToAssign() throws IOException {
		JavaFXTools.loadScene(FxmlView.KEY_ASSIGN);
	}

//	Changes pane to return pane
	@FXML
	private void switchToReturn() throws IOException {
		JavaFXTools.loadScene(FxmlView.KEY_RETURN);
	}

//	Executes user log out
	@FXML
	private void logoutButtonClicked() {
		dataService.logUserOut();
		dataService.clearCurrentUser();
		JavaFXTools.loadScene(FxmlView.LOGIN);
	}
	
//	Change pane to drive pane
	@FXML
	private void driveButtonClicked() throws IOException {
		JavaFXTools.loadScene(FxmlView.HOME);

	}

}
