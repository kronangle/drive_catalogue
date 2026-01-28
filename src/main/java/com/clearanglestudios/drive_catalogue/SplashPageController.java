package com.clearanglestudios.drive_catalogue;

import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.fxml.FXML;
import com.clearanglestudios.googleService.GoogleTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SplashPageController {

	private static final Logger logger = LogManager.getLogger(SplashPageController.class);

//    @FXML
//    public void initialize() {
//        // Run checks in background to keep UI fluid
//        CompletableFuture.runAsync(() -> {
//            try {
//                // Short delay so the user sees the splash (Optional, prevents flickering)
//                Thread.sleep(800); 
//
//                // Check Token
//                if (GoogleTools.isTokenValid()) {
//                    logger.info("Token found. Fetching profile...");
//                    
//                    // Fetch User Data
//                    GoogleTools.fetchUserInfo();
//                    
//                    // Success -> Go Home
//                    Platform.runLater(() -> JavaFXTools.loadScene(FxmlView.HOME));
//                } else {
//                    // Token invalid/missing
//                    throw new Exception("No valid token");
//                }
//            } catch (Exception e) {
//                // 4. Failure -> Go Login
//                logger.info("Startup check failed (" + e.getMessage() + "). Loading Login.");
//                Platform.runLater(() -> JavaFXTools.loadScene(FxmlView.LOGIN));
//            }
//        });
//    }

	@FXML
	public void initialize() {
//		Platform.runLater(() -> {
		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(800);
				// Check Token
				if (GoogleTools.isTokenValid()) {
					logger.info("Token found. Fetching profile...");

					// Fetch User Data
					GoogleTools.fetchUserInfo();

					// Go to Home
					Platform.runLater(() -> JavaFXTools.loadScene(FxmlView.HOME));
				} else {
					// Token invalid/missing
					throw new Exception("No valid token");
				}
			} catch (Exception e) {
				// Go to Login
				logger.info("Startup check failed (" + e.getMessage() + "). Loading Login.");
				Platform.runLater(() -> JavaFXTools.loadScene(FxmlView.LOGIN));
			}
		});

	}
}