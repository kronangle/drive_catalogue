package com.clearanglestudios.drive_catalogue;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.controlsfx.control.NotificationPane;

import com.clearanglestudios.googleService.GoogleTools;
import com.google.api.services.people.v1.PeopleService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JavaFX App
 */
public class App extends Application {

//	===================================================================
//	
//						DECLARATIONS
//	
//	===================================================================

//	UI Controls
	protected static Scene scene;
	protected static Stage stage;
	private static NotificationPane notificationPane;
	private static boolean isStartingUp = false;

//	Initialize Logger
	private final Logger logger = LogManager.getLogger(App.class);

//	Constants for styling
	private static final String WINDOW_TITLE = "CAS Drive Catalogue";
	private static final String PRIMARY_ICON_FOR_APP = "/com/clearanglestudios/Images/primary_icon.jpg";
	private static final int DEFAULT_WINDOW_WIDTH = 900;
	private static final int DEFAULT_WINDOW_LENGTH = 650;

//	For tests
	public static boolean isTestEnvironment = false;

//	===================================================================
//	
//						START UP
//	
//	===================================================================

//	Launch the app
	public static void main(String[] args) {
		launch();
	}

//  Starting point for Application
//	@Override
//	public void start(Stage stage) throws IOException {
//
//		if (!isTestEnvironment) {
//
//			isStartingUp = true;
//			logger.info(WINDOW_TITLE + " started");
//			App.stage = stage;
//
////		Initialize the NotificationPane with the first FXML
//			Parent initialRoot = loadFXML(FxmlView.LOGIN.getFxmlName());
//			notificationPane = new NotificationPane(initialRoot);
//			notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
//
////		Check if a user token is valid
////		findUser();
//
////      Create the scene with the NotificationPane as the root
//			scene = new Scene(notificationPane, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_LENGTH);
//			stage.setScene(scene);
//			stage.setResizable(true);
//
////		Set the icon and title
//			stage.getIcons().add(new Image(getClass().getResourceAsStream(PRIMARY_ICON_FOR_APP)));
//			stage.setTitle(WINDOW_TITLE);
//
////		Display stage and bring it to the front
//			stage.show();
//			bringStageToFront();
//
//			isStartingUp = false;
//		} else {
//
//			isStartingUp = true;
//			logger.info(WINDOW_TITLE + " started");
//			App.stage = stage;
//
////			Initialize the first FXML
//			Parent initialRoot = loadFXML(FxmlView.LOGIN.getFxmlName());
//			scene = new Scene(initialRoot);
//			stage.setScene(scene);
//			stage.setResizable(false);
////			Set the icon and title
//			stage.getIcons().add(new Image(getClass().getResourceAsStream(PRIMARY_ICON_FOR_APP)));
//			stage.setTitle(WINDOW_TITLE);
//
////			Display stage and bring it to the front
//			stage.show();
//			bringStageToFront();
//
//		}
//	}

//  Starting point for Application
    @Override
    public void start(Stage stage) throws IOException {

        if (!isTestEnvironment) {

            isStartingUp = true;
            logger.info(WINDOW_TITLE + " started");
            App.stage = stage;

            // Default destination: Splash Page
            FxmlView startView = FxmlView.SPLASH;

//            // Check if we can skip login
//            // Note: This pings Google. If it takes >1s, we might add a splash screen later.
//            if (GoogleTools.isTokenValid()) {
//                logger.info("Valid token found. Skipping login.");
//                startView = FxmlView.HOME;
//            } else {
//                logger.info("No valid token. Showing login.");
//            }

            // Initialize the NotificationPane with the DETERMINED FXML
            Parent initialRoot = loadFXML(startView.getFxmlName());
            
            notificationPane = new NotificationPane(initialRoot);
            notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);

            // Create the scene with the NotificationPane as the root
            scene = new Scene(notificationPane, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_LENGTH);
            
            // [Standardization] Force Global CSS on startup
            String cssPath = App.class.getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            stage.setScene(scene);
            stage.setResizable(true);

            // Set the icon and title
            stage.getIcons().add(new Image(getClass().getResourceAsStream(PRIMARY_ICON_FOR_APP)));
            stage.setTitle(WINDOW_TITLE);

            // Display stage and bring it to the front
            stage.show();
            bringStageToFront();

            isStartingUp = false;
        } else {
            // Test Environment logic (Keep as is)
            isStartingUp = true;
            logger.info(WINDOW_TITLE + " started (Test Mode)");
            App.stage = stage;

            Parent initialRoot = loadFXML(FxmlView.LOGIN.getFxmlName());
            scene = new Scene(initialRoot);
            stage.setScene(scene);
            stage.setResizable(false);
            
            stage.getIcons().add(new Image(getClass().getResourceAsStream(PRIMARY_ICON_FOR_APP)));
            stage.setTitle(WINDOW_TITLE);

            stage.show();
            bringStageToFront();
        }
    }
	
//	Forces the window to the front of the screen
	public static void bringStageToFront() {
		stage.setAlwaysOnTop(true); // Temporarily make the stage always on top
		stage.toFront();
		stage.setAlwaysOnTop(false); // Reset to normal behaviour

	}

//	===================================================================
//	
//					USER MANAGEMENT METHODS
//	
//	===================================================================

////	Check if a valid token is present and try to set the logged in user
//	private void findUser() {
//		// Get the user's name
//		PeopleService peopleService;
//		try {
//			peopleService = GoogleTools.getPeopleService();
//			GoogleTools.setCurrentUser(
//					peopleService.people().get("people/me").setPersonFields("names,emailAddresses").execute());
//
//		} catch (GeneralSecurityException e) {
//			logger.warn("Failed to get Person data");
//			logger.error("GeneralSecurityException ", e);
//
//		} catch (IOException e) {
//			logger.warn("Failed to get Person data");
//			logger.error("IOException ", e);
//		}
//	}

//	Indicates if Application is starting up
	public static boolean isStartingUp() {
		return isStartingUp;
	}

//	===================================================================
//	
//					JAVAFX METHODS
//	
//	===================================================================

//	Used to switch the GUI's active scene
	static void setRoot(String fxml) throws IOException {
		Parent newRoot = loadFXML(fxml);
//		Update the NotificationPane's content
		notificationPane.setContent(newRoot);
		stage.sizeToScene();
		stage.centerOnScreen();
	}

//  Used to load the FXML file for new scene
	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

//	===================================================================
//	
//					NOTIFICATION METHODS
//	
//	===================================================================

//	Show a notification
	public static void showNotification(String message) {
		// Check which thread call is on
        if (Platform.isFxApplicationThread()) {
            if (notificationPane != null) {
                notificationPane.setText(message);
                notificationPane.show();
                
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> hideNotification());
                delay.play();
            }
        } else {
            Platform.runLater(() -> showNotification(message));
        }
	}

//	Hide the notification
	public static void hideNotification() {
		notificationPane.hide();
	}

//	Check if a notification is showing
	public static boolean isNotificationShowing() {
		return notificationPane.isShowing();
	}

//	===================================================================
//	
//						GETTERS & SETTERS
//	
//	===================================================================
	public static Stage getStage() {
		return stage;
	}

	public static NotificationPane getNotificationPane() {
		return notificationPane;
	}

}