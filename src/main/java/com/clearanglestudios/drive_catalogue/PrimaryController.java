package com.clearanglestudios.drive_catalogue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.IDataService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

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
	@FXML
	private Label lastSyncLabel_Primary;
	@FXML
	private Button refreshButton_Primary;
	@FXML
	private Button keyButton_Primary;

//	-------------------------------------------------------------------------------------

//	Initialize Logger
	private static final Logger logger = LogManager.getLogger(PrimaryController.class);

//	-------------------------------------------------------------------------------------

//	Logged in user label
	private static final String loggedInLabelText = "Logged in as: ";
	private static final String loggedInLabelSpacing = "  ";
	
//	-------------------------------------------------------------------------------------
	
//	Data Management
	private final IDataService dataService = App.getDataService();

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
				+ dataService.getCurrentUserName() + loggedInLabelSpacing);
//		-------------------------------------------------------------------------------------
//		Disable ingest button if user is not a part of the IT department
		checkPermsForIngestButton();
//		Disable key button if user is not a part of the Loggers group	
		checkPermsForKeyButton();
//		-------------------------------------------------------------------------------------
		lastSyncLabel_Primary.setText("  Last Synced: " + dataService.getLastSyncTime() + "  ");
		logger.info("COMPLETED Initialising PrimaryController");
	}

//	Disable ingest button if user is not a part of the IT department
	private void checkPermsForIngestButton() {
		String currentUserEmail = dataService.getCurrentUserEmail();
//		Compare email address against the lookup table from the spreadsheet
		try {
			List<String> itEmailAddresses = dataService.getITEmailAddresses();
			if (!itEmailAddresses.contains(currentUserEmail)) {
				ingestButton_Primary.setDisable(true);
			}
		} catch (GeneralSecurityException e) {
			dataService.logGeneralSecurityException("IT", e);
		} catch (IOException e) {
			dataService.logIOException("IT", e);
		}
	}
	
	// Disable Key button if user is not in the allowed loggers list
    private void checkPermsForKeyButton() {
        String currentUserName = dataService.getCurrentUserName(); 
        
        try {
            List<String> allowedLoggers = dataService.getKeyLoggers();
            
            boolean isAllowed = false;
            for (String loggerName : allowedLoggers) {
                if (loggerName.equalsIgnoreCase(currentUserName)) {
                    isAllowed = true;
                    break;
                }
            }

            if (!isAllowed) {
                if (keyButton_Primary != null) {
                	keyButton_Primary.setDisable(true);
                    logger.info("User '" + currentUserName + "' is not authorized for Key Management. Button disabled.");
                }
            } else {
                logger.info("User '" + currentUserName + "' is authorized for Key Management.");
            }
            
        } catch (GeneralSecurityException e) {
        	dataService.logGeneralSecurityException("Key Loggers", e);
        } catch (IOException e) {
        	dataService.logIOException("Key Loggers", e);
        }
    }

//	=====================================================================================
//	
//									DATA MANAGEMENT
//	
//	=====================================================================================
	
	@FXML
	private void refreshButtonClicked() {
		logger.info("Manual Refresh Triggered from Home Page");
//        -------------------------------------
        dataService.clearCache();
        lastSyncLabel_Primary.setText("  Last Synced: --:--  ");
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
		dataService.logUserOut();
		dataService.clearCurrentUser();
		JavaFXTools.loadScene(FxmlView.LOGIN);
	}
	
//	Change pane to key pane
	@FXML
	private void keyButtonClicked() throws IOException {
		JavaFXTools.loadScene(FxmlView.KEY_HOME);

	}

}
