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
		App.hideNotification();
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
//		Compare email address against a lookup table from the spreadsheet
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
		App.showNotification("Loading data...");
		logger.info("Trying to switch to DriveAssign from Primary");
//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
				App.setRoot("DriveAssign");
			} catch (IOException e) {
				logger.warn("Failed to load DriveAssign pane.");
				logger.error("IOException", e);
				App.showNotification("Failed to load DriveAssign pane.");
			}
		});
		delay.play();
	}

//	Changes pane to ingest pane
	@FXML
	private void switchToIngest() throws IOException {
		App.showNotification("Loading data...");
		logger.info("Trying to switch to DriveIngest from Primary");
//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
				App.setRoot("DriveIngest");
			} catch (IOException e) {
				logger.warn("Failed to load DriveIngest pane.");
				logger.error("IOException", e);
				App.showNotification("Failed to load DriveIngest pane.");
			}
		});
		delay.play();
	}

//	Changes pane to return pane
	@FXML
	private void switchToReturn() throws IOException {
		App.showNotification("Loading data...");
		logger.info("Trying to switch to DriveReturn from Primary");
//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
				App.setRoot("DriveReturn");
			} catch (IOException e) {
				logger.warn("Failed to load DriveReturn pane.");
				logger.error("IOException", e);
				App.showNotification("Failed to load DriveReturn pane.");
			}
		});
		delay.play();
	}

//	Changes pane to query pane
	@FXML
	private void switchToQuery() throws IOException {
		App.showNotification("Loading data...");
		logger.info("Trying to switch to DriveQuery from Primary");
//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
				App.setRoot("DriveQuery");
			} catch (IOException e) {
				logger.warn("Failed to load DriveQuery pane.");
				logger.error("IOException", e);
				App.showNotification("Failed to load DriveQuery pane.");
			}
		});
		delay.play();
	}

//	Executes user log out
	@FXML
	private void logoutButtonClicked() {
		try {
			GoogleTools.logUserOut();
			LoginPageController.setCurrentUser(null);
			switchToLoginPage();
		} catch (IOException e) {
			logger.warn("Failed to logout user from logoutButtonClicked");
			logger.error("IOException", e);
			App.showNotification("Failed to logout user");
		}
	}

//	Changes pane to login page
	private void switchToLoginPage() throws IOException {
		App.showNotification("Loading Login page...");
		logger.info("Trying to switch to LoginPage from Primary");
//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
				App.setRoot("LoginPage");
			} catch (IOException e) {
				logger.warn("Failed to load LoginPage pane.");
				logger.error("IOException", e);
				App.showNotification("Failed to load LoginPage pane.");
			}
		});
		delay.play();
	}

}
