package com.clearanglestudios.drive_catalogue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.googleService.IDataService;
import com.clearanglestudios.googleService.KeyService;
import com.clearanglestudios.objects.Crew;
import com.clearanglestudios.objects.Key;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class KeyAssignController {

    private static final Logger logger = LogManager.getLogger(KeyAssignController.class);

    // ===================================================================
    //                      DECLARATIONS
    // ===================================================================

    // --- FXML Fields ---
    @FXML private ComboBox<Key> keyComboBox_KeyAssign;
    @FXML private ComboBox<String> crewComboBox_KeyAssign;
    @FXML private TextArea keyNotesTextArea_KeyAssign;
    @FXML private Label loggedInLabel_KeyAssign;

    // --- Filter Tools (Matching DriveAssignController) ---
    private ObservableList<Key> observableKeyList = FXCollections.observableArrayList();
    private String oldKeyFilter = "";
    
    private ObservableList<String> observableCrewList = FXCollections.observableArrayList();
    private String oldCrewFilter = "";
    
    private boolean isUpdatingKeys = false;
    private boolean isUpdatingCrew = false;

//	-------------------------------------------------------------------------------------
	
//	Data Management
	private final IDataService dataService = App.getDataService();
    
    // ===================================================================
    //                      START UP
    // ===================================================================

    @FXML
    public void initialize() {
        logger.info("Initializing KeyAssignController");
        loggedInLabel_KeyAssign.setText("Logged in as: " + dataService.getCurrentUserName());

        CompletableFuture.runAsync(() -> {
            try {
                // Fetch Data
                List<Key> allKeys = KeyService.getAllKeys();
                List<Crew> crewObjects = dataService.getCrewData();

                // Prepare Crew Names List
                List<String> crewNames = new ArrayList<>();
                for (Crew crew : crewObjects) {
                    crewNames.add(crew.getName().trim());
                }
                Collections.sort(crewNames);

                Platform.runLater(() -> {
                    // Add to Master Lists
                    observableKeyList.addAll(allKeys);
                    observableCrewList.addAll(crewNames);

                    // Setup ComboBoxes
                    setupComboBoxes();
                });

            } catch (Exception e) {
                logger.error("Failed to load Key/Crew data", e);
                Platform.runLater(() -> App.showNotification("Error loading data: " + e.getMessage()));
            }
        });
    }

    // Adds options and listeners (Matching DriveAssignController pattern)
    private void setupComboBoxes() {
        // 1. Populate initial lists
        keyComboBox_KeyAssign.setItems(observableKeyList);
        crewComboBox_KeyAssign.setItems(observableCrewList);

        // 2. Add Listeners for Filtering
        keyComboBox_KeyAssign.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            filterKeys(keyComboBox_KeyAssign, observableKeyList, newValue);
        });

        crewComboBox_KeyAssign.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            filterCrew(crewComboBox_KeyAssign, observableCrewList, newValue);
        });
    }

    // ===================================================================
    //                  DATA MANAGEMENT & FILTERING
    // ===================================================================

    // Logic for Key combo box live filtering
    private void filterKeys(ComboBox<Key> comboBox, ObservableList<Key> originalList, String filter) {
        if (!isUpdatingKeys) {
            ObservableList<Key> filteredList = FXCollections.observableArrayList();
            String trimmedFilter = (filter == null) ? "" : filter.trim();

            // If selection matches text exactly, ignore filter (user selected an item)
            Key selection = comboBox.getSelectionModel().getSelectedItem();
            if (selection != null && selection.toString().equals(trimmedFilter)) {
                return;
            }

            if (!trimmedFilter.isBlank()) {
                if (!trimmedFilter.equals(oldKeyFilter)) {
                    for (Key key : originalList) {
                        // Check Key Name or Tag
                        if (key.toString().toLowerCase().contains(trimmedFilter.toLowerCase())) {
                            filteredList.add(key);
                        }
                    }
                    oldKeyFilter = trimmedFilter;
                }
                // Update Items
                comboBox.setItems(filteredList);
                
                // Keep the dropdown open if we have results
                if (!filteredList.isEmpty()) {
                    comboBox.show();
                } else {
                    comboBox.hide();
                }
            } else {
                // Restore full list
                comboBox.setItems(originalList);
            }
        }
    }

    // Logic for Crew combo box live filtering
    private void filterCrew(ComboBox<String> comboBox, ObservableList<String> originalList, String filter) {
        if (!isUpdatingCrew) {
            ObservableList<String> filteredList = FXCollections.observableArrayList();
            String trimmedFilter = (filter == null) ? "" : filter.trim();
            
            // If selection matches text exactly, ignore filter
            String selection = comboBox.getSelectionModel().getSelectedItem();
            if (selection != null && selection.equals(trimmedFilter)) {
                return;
            }

            if (!trimmedFilter.isBlank()) {
                if (!trimmedFilter.equals(oldCrewFilter)) {
                    for (String name : originalList) {
                        if (name.toLowerCase().contains(trimmedFilter.toLowerCase())) {
                            filteredList.add(name);
                        }
                    }
                    oldCrewFilter = trimmedFilter;
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
        // Clear selections and reset to full lists
        isUpdatingKeys = true; // Prevent listener firing during reset
        isUpdatingCrew = true;
        
        keyComboBox_KeyAssign.getSelectionModel().clearSelection();
        keyComboBox_KeyAssign.getEditor().clear();
        keyComboBox_KeyAssign.setItems(observableKeyList); // Restore full list

        crewComboBox_KeyAssign.getSelectionModel().clearSelection();
        crewComboBox_KeyAssign.getEditor().clear();
        crewComboBox_KeyAssign.setItems(observableCrewList); // Restore full list

        keyNotesTextArea_KeyAssign.clear();
        
        isUpdatingKeys = false;
        isUpdatingCrew = false;
    }

    // ===================================================================
    //                  JAVAFX METHODS
    // ===================================================================

    @FXML
    private void assignButtonClicked() {
        Key selectedKey = null;
        
        if (keyComboBox_KeyAssign.getSelectionModel().getSelectedItem() instanceof Key) {
            selectedKey = keyComboBox_KeyAssign.getSelectionModel().getSelectedItem();
        }
        
        if (selectedKey == null) {
            String typedText = keyComboBox_KeyAssign.getEditor().getText();
            for (Key k : observableKeyList) {
                if (k.toString().equalsIgnoreCase(typedText)) {
                    selectedKey = k;
                    break;
                }
            }
        }

        if (selectedKey == null) {
            App.showNotification("Please select a valid Key from the list.");
            return;
        }

        String selectedCrew = crewComboBox_KeyAssign.getValue();
        if (selectedCrew == null || selectedCrew.isEmpty()) {
            selectedCrew = crewComboBox_KeyAssign.getEditor().getText();
        }

        if (selectedCrew == null || selectedCrew.isEmpty()) {
            App.showNotification("Please select a Crew member.");
            return;
        }

        String notes = keyNotesTextArea_KeyAssign.getText();
        
        selectedKey.setStatus("Out");
        selectedKey.setAssignedTo(selectedCrew);
        selectedKey.setLoggedBy(dataService.getCurrentUserName());
        selectedKey.setDateOfLog(dataService.getTodaysDate());
        selectedKey.setNotes(notes);

        logger.info("Queuing Key Update: " + selectedKey.getItemName() + " -> " + selectedCrew);
        KeyService.queueKeyUpdate(selectedKey);

        resetForm();
        App.showNotification("Key assigned! Ready for next.");
    }

    @FXML
    private void cancelButtonClicked() {
        resetForm();
        JavaFXTools.loadScene(FxmlView.KEY_HOME);
    }
}