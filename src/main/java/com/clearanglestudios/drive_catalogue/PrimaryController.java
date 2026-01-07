package com.clearanglestudios.drive_catalogue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.NotificationPane;

import com.clearanglestudios.googleService.GoogleTools;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class PrimaryController {

//	=====================================================================================
//	
//										DECLARATIONS
//	
//	=====================================================================================

//	User input fields and controls
	@FXML
	private Button assignButton_Primary;
	@FXML
	private Button ingestButton_Primary;
	@FXML
	private Button returnButton_Primary;
	@FXML
	private Button queryButton_Primary;
	@FXML
	private Label loggedInLabel_primary;

//	-------------------------------------------------------------------------------------

//	Initialize Logger
	private static final Logger logger = LogManager.getLogger(PrimaryController.class);

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
		logger.info("Initialising PrimaryController");
//		App.hideNotification();
//		-------------------------------------------------------------------------------------
//		Set the label to the current user's name
		loggedInLabel_primary.setText(loggedInLabelSpacing + loggedInLabelText
				+ LoginPageController.getCurrentUserName() + loggedInLabelSpacing);
//		-------------------------------------------------------------------------------------
//		Hide ingest button if user is not a part of the IT department
		checkPermsForIngestButton();
//		-------------------------------------------------------------------------------------
		logger.info("COMPLETED Initialising PrimaryController");
	}

//	Disable ingest button if user is not a part of the IT department
	private void checkPermsForIngestButton() {
		String currentUserEmail = LoginPageController.getCurrentUserEmail();
//		Compare email address against the lookup table from the spreadsheet
		try {
			ArrayList<String> itEmailAddresses = GoogleTools.getITEmailAddresses();
			if (!itEmailAddresses.contains(currentUserEmail)) {
				ingestButton_Primary.setDisable(true);
			}
		} catch (GeneralSecurityException e) {
			GoogleTools.logGeneralSecurityException("IT", e);
		} catch (IOException e) {
			GoogleTools.logIOException("IT", e);
		}
	}

//	=====================================================================================
//	
//									JAVAFX METHODS
//	
//	=====================================================================================

//	Changes pane to assign pane
	@FXML
	private void switchToAssign() throws IOException {
		JavaFXTools.loadScene(FxmlView.ASSIGN);
	}

//	Changes pane to ingest pane
	@FXML
	private void switchToIngest() throws IOException {
		JavaFXTools.loadScene(FxmlView.INGEST);
	}

//	Changes pane to return pane
	@FXML
	private void switchToReturn() throws IOException {
		JavaFXTools.loadScene(FxmlView.RETURN);
	}

//	Changes pane to query pane
	@FXML
	private void switchToQuery() throws IOException {
		JavaFXTools.loadScene(FxmlView.QUERY);

	}

//	Executes user log out
	@FXML
	private void logoutButtonClicked() {
		GoogleTools.logUserOut();
		LoginPageController.setCurrentUser(null);
		JavaFXTools.loadScene(FxmlView.LOGIN);
	}

}
