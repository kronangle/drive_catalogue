package com.clearanglestudios.drive_catalogue;

import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.fxml.FXML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.IDataService;

public class SplashPageController {
	

	private static final Logger logger = LogManager.getLogger(SplashPageController.class);
	private final IDataService dataService = App.getDataService();

	@FXML
	public void initialize() {
		CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(800);
				// Check Token
				if (dataService.isTokenValid()) {
					logger.info("Token found. Fetching profile...");

					// Fetch User Data
					dataService.fetchUserInfo();

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