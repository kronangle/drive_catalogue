package com.clearanglestudios.drive_catalogue;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.IDataService;
import com.clearanglestudios.objects.Key;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class KeyReturnController {

    private static final Logger logger = LogManager.getLogger(KeyReturnController.class);

    // ===================================================================
    //                      DECLARATIONS
    // ===================================================================

    @FXML private ComboBox<Key> keyComboBox_KeyReturn;
    @FXML private Label loggedInLabel_KeyRetrun; // Note: typo in FXML ID "KeyRetrun" preserved here

    // --- Filter Tools ---
    private ObservableList<Key> observableKeyList = FXCollections.observableArrayList();
    private String oldKeyFilter = "";
    private boolean isUpdatingKeys = false;
    
//	-------------------------------------------------------------------------------------
	
//	Data Management
	private final IDataService dataService = App.getDataService();

    // ===================================================================
    //                      START UP
    // ===================================================================

    @FXML
    public void initialize() {
        logger.info("Initializing KeyReturnController");

        // 1. Set User Label
        loggedInLabel_KeyRetrun.setText("Logged in as: " + dataService.getCurrentUserName());

        // 2. Load Data (Async)
        CompletableFuture.runAsync(() -> {
            try {
                List<Key> allKeys = dataService.getAllKeys();

                // OPTIONAL: Sort keys so "Out" keys are at the top for easier access
                allKeys.sort(Comparator.comparing(Key::getStatus).reversed() // "Out" comes before "In"
                        .thenComparing(Key::getItemName));

                // Update UI on FX Thread
                Platform.runLater(() -> {
                    observableKeyList.addAll(allKeys);
                    setupComboBoxes();
                });

            } catch (Exception e) {
                logger.error("Failed to load Key data", e);
                Platform.runLater(() -> App.showNotification("Error loading data: " + e.getMessage()));
            }
        });
    }

    private void setupComboBoxes() {
        // 1. Populate initial list
        keyComboBox_KeyReturn.setItems(observableKeyList);

        // 2. Add Listener for Filtering
        keyComboBox_KeyReturn.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            filterKeys(keyComboBox_KeyReturn, observableKeyList, newValue);
        });
    }

    // ===================================================================
    //                  FILTERING LOGIC
    // ===================================================================

    private void filterKeys(ComboBox<Key> comboBox, ObservableList<Key> originalList, String filter) {
        if (!isUpdatingKeys) {
            ObservableList<Key> filteredList = FXCollections.observableArrayList();
            String trimmedFilter = (filter == null) ? "" : filter.trim();

            // Ignore if selection matches text exactly
            Key selection = comboBox.getSelectionModel().getSelectedItem();
            if (selection != null && selection.toString().equals(trimmedFilter)) {
                return;
            }

            if (!trimmedFilter.isBlank()) {
                if (!trimmedFilter.equals(oldKeyFilter)) {
                    for (Key key : originalList) {
                        if (key.toString().toLowerCase().contains(trimmedFilter.toLowerCase())) {
                            filteredList.add(key);
                        }
                    }
                    oldKeyFilter = trimmedFilter;
                }
                comboBox.setItems(filteredList);
                
                if (!filteredList.isEmpty()) {
                    comboBox.show();
                } else {
                    comboBox.hide();
                }
            } else {
                comboBox.setItems(originalList);
            }
        }
    }

    private void resetForm() {
        isUpdatingKeys = true;
        keyComboBox_KeyReturn.getSelectionModel().clearSelection();
        keyComboBox_KeyReturn.getEditor().clear();
        keyComboBox_KeyReturn.setItems(observableKeyList);
        isUpdatingKeys = false;
    }

    // ===================================================================
    //                  JAVAFX METHODS
    // ===================================================================

    @FXML
    private void returnButtonClicked() {
        // 1. Validate Input (Handle String vs Object safety check)
        Key selectedKey = null;

        // Check if object selected
        if (keyComboBox_KeyReturn.getSelectionModel().getSelectedItem() instanceof Key) {
            selectedKey = keyComboBox_KeyReturn.getSelectionModel().getSelectedItem();
        }
        
        // Fallback: Check if typed text matches an object
        if (selectedKey == null) {
            String typedText = keyComboBox_KeyReturn.getEditor().getText();
            for (Key k : observableKeyList) {
                if (k.toString().equalsIgnoreCase(typedText)) {
                    selectedKey = k;
                    break;
                }
            }
        }

        if (selectedKey == null) {
            App.showNotification("Please select a valid Key.");
            return;
        }

        // 2. Queue Return (Service handles Archiving + Clearing)
        logger.info("Queuing Return for: " + selectedKey.getItemName());
        dataService.queueKeyReturn(selectedKey);

        // 3. Reset
        resetForm();
        App.showNotification("Key returned successfully.");
    }

    @FXML
    private void cancelButtonClicked() {
        resetForm();
        JavaFXTools.loadScene(FxmlView.KEY_HOME);
    }
}