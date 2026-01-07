package com.clearanglestudios.drive_catalogue;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.GoogleTools;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.util.Duration;

public class LoginPageController {
	
//	=====================================================================================
//	
//										DECLARATIONS
//	
//	=====================================================================================

//	The logged in user account
	private static Person currentUser;

//	-------------------------------------------------------------------------------------
	
//	Initialize Logger
	private static final Logger logger = LogManager.getLogger(LoginPageController.class);

//	=====================================================================================
//	
//										START UP
//	
//	=====================================================================================
	
//	Runs before displaying the pane
	@FXML
	public void initialize() {
		logger.info("Initialising LoginPage");

		if (!App.isStartingUp()) {
			App.hideNotification();
		}

		logger.info("COMPLETED Initialising LoginPage");
	}

//	=====================================================================================
//	
//									USER MANAGEMENT METHODS
//	
//	=====================================================================================

//	Sets the current user
	public static void setCurrentUser(Person currentUser) {
		LoginPageController.currentUser = currentUser;
	}

//	Returns a authenticate Person object
	private static Person getCurrentUser() throws GeneralSecurityException, IOException {
//		Get the user's name
		PeopleService peopleService;
		peopleService = GoogleTools.getPeopleService();

		return peopleService.people().get("people/me").setPersonFields("names,emailAddresses").execute();

	}

//	Returns the current user's name
	public static String getCurrentUserName() {
		return currentUser.getNames().get(0).getDisplayName();
	}

//	Returns the current user's email address
	public static String getCurrentUserEmail() {
		return currentUser.getEmailAddresses().get(0).getValue();
	}

//	=====================================================================================
//	
//									JAVAFX METHODS
//	
//	=====================================================================================
	
//	Confirms or requests user login
	@FXML
	private void loginButtonClicked() {
		App.showNotification("Logging in to Google Account...");
		logger.info("Logging into Google Account from loginButtonClicked");
//		Add a short delay before switching the pane
		PauseTransition delay = new PauseTransition(Duration.seconds(1)); // 1-second delay
		delay.setOnFinished(event -> {
			try {
				if (currentUser != null) {
					JavaFXTools.loadScene(FxmlView.HOME);
				} else {
					logger.info("No user set");
					logger.info("Attempting to getCurrentUser");
					currentUser = getCurrentUser();
					JavaFXTools.loadScene(FxmlView.HOME);
					App.bringStageToFront();
				}

			} catch (IOException e) {
				logger.warn("Failed to login and switch to Home Page");
				logger.error("IOException - ", e);
				App.showNotification("Failed to login and switch to Home Page");
				
			} catch (GeneralSecurityException e) {
				logger.warn("Failed to login and switch to Home Page");
				logger.error("GeneralSecurityException - ", e);
				App.showNotification("Failed to login and switch to Home Page");
			}
		});
		delay.play();
	}
	
}
