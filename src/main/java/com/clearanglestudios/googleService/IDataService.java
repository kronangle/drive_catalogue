package com.clearanglestudios.googleService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import com.clearanglestudios.objects.Crew;
import com.clearanglestudios.objects.Drive;
import com.clearanglestudios.objects.Key;
import com.clearanglestudios.objects.SheetUpdate;

public interface IDataService {

//    --- Authentication / User Session ---
    boolean isTokenValid();
    void fetchUserInfo() throws IOException, GeneralSecurityException;
    String getCurrentUserName();
    String getCurrentUserEmail();
    void logUserOut();
    void clearCurrentUser();

//    --- Drive Data ---
    List<Drive> getDriveData() throws GeneralSecurityException, IOException;
    List<String> getFilteredDriveNames(String status) throws GeneralSecurityException, IOException;
    List<String> getFilteredDriveNames(String status, String size) throws GeneralSecurityException, IOException;
    String getFilteredCrewName(String driveNameToMatch) throws GeneralSecurityException, IOException;

//    --- Crew & Dept Data ---
    List<Crew> getCrewData() throws GeneralSecurityException, IOException;
    
//    --- Ingesting Data ---
    List<String> getPcNames() throws GeneralSecurityException, IOException;
    List<String> getITEmailAddresses() throws GeneralSecurityException, IOException;

//    --- Key Module ---
    List<Key> getAllKeys() throws IOException, GeneralSecurityException;
    List<String> getKeyLoggers() throws IOException, GeneralSecurityException;

//    --- Updates & Logic ---
    void queueSheetUpdate(SheetUpdate ticket);
    void queueKeyUpdate(Key key);
    void queueKeyReturn(Key key);
    Map<String, Boolean> verifyInfo(String[] info) throws GeneralSecurityException, IOException;
    
//    --- Cache Management ---
    void clearCache();
    String getLastSyncTime();
    
 // --- Error Handling ---
    void logGeneralSecurityException(String context, GeneralSecurityException e);
    void logIOException(String context, IOException e);
	String getTodaysDate();
	boolean doesDriveNameExist(String driveName) throws GeneralSecurityException, IOException;
	boolean doesCrewNameExist(String crewName) throws GeneralSecurityException, IOException;
}