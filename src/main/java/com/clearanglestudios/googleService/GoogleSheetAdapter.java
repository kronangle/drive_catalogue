package com.clearanglestudios.googleService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import com.clearanglestudios.googleService.IDataService;
import com.clearanglestudios.objects.Crew;
import com.clearanglestudios.objects.Drive;
import com.clearanglestudios.objects.Key;
import com.clearanglestudios.objects.SheetUpdate;

/**
 * The Bridge between the modern Interface-based architecture 
 * and the legacy Static Utility classes (GoogleTools & KeyService).
 */
public class GoogleSheetAdapter implements IDataService {

    public GoogleSheetAdapter() {
    }

    // =================================================================
    //                       AUTHENTICATION
    // =================================================================

    @Override
    public boolean isTokenValid() {
        return GoogleTools.isTokenValid();
    }

    @Override
    public void fetchUserInfo() throws IOException, GeneralSecurityException {
        GoogleTools.fetchUserInfo();
    }

    @Override
    public String getCurrentUserName() {
        return GoogleTools.getCurrentUserName();
    }

    @Override
    public String getCurrentUserEmail() {
        return GoogleTools.getCurrentUserEmail();
    }

    @Override
    public void logUserOut() {
        GoogleTools.logUserOut();
    }

    @Override
    public void clearCurrentUser() {
        GoogleTools.clearCurrentUser();
    }

    // =================================================================
    //                       DRIVE MODULE
    // =================================================================

    @Override
    public List<Drive> getDriveData() throws GeneralSecurityException, IOException {
        return GoogleTools.getDriveData();
    }

    @Override
    public List<String> getFilteredDriveNames(String status) throws GeneralSecurityException, IOException {
        return GoogleTools.getFilteredDriveNames(status);
    }

    @Override
    public List<String> getFilteredDriveNames(String status, String size) throws GeneralSecurityException, IOException {
        return GoogleTools.getFilteredDriveNames(status, size);
    }

    @Override
    public String getFilteredCrewName(String driveNameToMatch) throws GeneralSecurityException, IOException {
        return GoogleTools.getFilteredCrewName(driveNameToMatch);
    }

    @Override
    public void queueSheetUpdate(SheetUpdate ticket) {
        GoogleTools.queueSheetUpdate(ticket);
    }

    @Override
    public Map<String, Boolean> verifyInfo(String[] info) throws GeneralSecurityException, IOException {
        return GoogleTools.verifyInfo(info);
    }

    // =================================================================
    //                       CREW & INGEST MODULE
    // =================================================================

    @Override
    public List<Crew> getCrewData() throws GeneralSecurityException, IOException {
        return GoogleTools.getCrewData();
    }

    @Override
    public List<String> getPcNames() throws GeneralSecurityException, IOException {
        return GoogleTools.getPcNames();
    }

    @Override
    public List<String> getITEmailAddresses() throws GeneralSecurityException, IOException {
        return GoogleTools.getITEmailAddresses();
    }

    // =================================================================
    //                       KEY MODULE
    // =================================================================

    @Override
    public List<Key> getAllKeys() throws IOException, GeneralSecurityException {
        return KeyService.getAllKeys();
    }

    @Override
    public void queueKeyUpdate(Key key) {
        KeyService.queueKeyUpdate(key);
    }

    @Override
    public void queueKeyReturn(Key key) {
        KeyService.queueKeyReturn(key);
    }
    
    @Override
    public List<String> getKeyLoggers() throws IOException, GeneralSecurityException {
        return KeyService.getKeyLoggers();
    }

    // =================================================================
    //                       SYSTEM / CACHE
    // =================================================================

    @Override
    public void clearCache() {
        GoogleTools.clearCache();
    }

    @Override
    public String getLastSyncTime() {
        return GoogleTools.getLastSyncTime();
    }

    @Override
	public String getTodaysDate() {
		return GoogleTools.getTodaysDate();
	}
	
    // =================================================================
    //                       ERROR HANDLING
    // =================================================================
    
    @Override
	public void logGeneralSecurityException(String context, GeneralSecurityException e) {
		GoogleTools.logGeneralSecurityException(context, e);
	}

	@Override
	public void logIOException(String context, IOException e) {
		GoogleTools.logIOException(context, e);
	}

	@Override
	public boolean doesDriveNameExist(String driveName) throws GeneralSecurityException, IOException {
		return GoogleTools.doesDriveNameExist(driveName);
	}

	@Override
	public boolean doesCrewNameExist(String crewName) throws GeneralSecurityException, IOException {
		return GoogleTools.doesCrewNameExist(crewName);
	}

	@Override
	public boolean isAdmin(String email) throws GeneralSecurityException, IOException {
		return GoogleTools.isAdmin(email);
	}

	
}