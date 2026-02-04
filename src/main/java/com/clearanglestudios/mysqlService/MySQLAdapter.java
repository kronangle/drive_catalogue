package com.clearanglestudios.mysqlService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Updated Package Location
import com.clearanglestudios.googleService.IDataService;
import com.clearanglestudios.objects.Crew;
import com.clearanglestudios.objects.Drive;
import com.clearanglestudios.objects.Key;
import com.clearanglestudios.objects.SheetUpdate;

public class MySQLAdapter implements IDataService {

	private static final Logger logger = LogManager.getLogger(MySQLAdapter.class);

	// --- Database Configuration ---
	private static final String DB_URL = "jdbc:mysql://localhost:3306/cas_catalogue";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "nFzxpn9z4212"; 

	public MySQLAdapter() {
		logger.info("MySQLAdapter initialized. Application is running in Database Mode.");
	}

	// =================================================================
	

	@Override
	public List<Drive> getDriveData() throws GeneralSecurityException, IOException {
		List<Drive> drives = new ArrayList<>();
		String query = "SELECT * FROM drives";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			logger.info("Connected to MySQL. Fetching drives...");

			while (rs.next()) {
				Drive drive = new Drive(String.valueOf(rs.getInt("id")),
						rs.getString("name"), 
						rs.getString("size"), 
						rs.getString("status"), 
						rs.getString("assignment"), 
						rs.getString("crew"), 
						rs.getString("dateOfLastLogged"), 
						rs.getString("loggedByUser"), 
						rs.getString("driveTag"),
						rs.getString("casTag") 
				);
				drives.add(drive);
			}
			logger.info("Retrieved " + drives.size() + " drives from SQL.");

		} catch (SQLException e) {
			logger.error("Database Error", e);
			throw new IOException("Failed to fetch data from MySQL", e);
		}

		return drives;
	}

	// =================================================================
    //                     VALIDATION METHODS
    // =================================================================

    @Override
    public boolean doesDriveNameExist(String driveName) throws GeneralSecurityException, IOException {
        String query = "SELECT COUNT(*) FROM drives WHERE name = ?";
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement perpareStatement = connection.prepareStatement(query)) {
            
            perpareStatement.setString(1, driveName);
            try (ResultSet resultSet = perpareStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Error checking drive existence", e);
        }
        return false;
    }

    @Override
    public Map<String, Boolean> verifyInfo(String[] info) {
        Map<String, Boolean> results = new HashMap<>();
        
        try {
            boolean driveExists = doesDriveNameExist(info[0]);
            results.put("DriveName", driveExists);
            results.put("CrewName", true); 
            results.put("PcName", true);   
            results.put("Details", true);  
        } catch (Exception e) {
            logger.error("Validation failed", e);
            results.put("DriveName", false);
        }
        
        return results;
    }
    
 // =================================================================
    //                     UPDATE METHOD
    // =================================================================

    @Override
    public void queueSheetUpdate(SheetUpdate ticket) {
      CompletableFuture.runAsync(() -> {
            String[] info = ticket.getInfo();
            String driveName = info[0]; 
            String status = info[1];    
            String crew = info[2];      
            String assignment = info[3];
            
            String update = "UPDATE drives SET status = ?, assignment = ?, crew = ?, dateOfLastLogged = ?, loggedByUser = ? WHERE name = ?";

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(update)) {

                logger.info("Updating MySQL for drive: " + driveName);

                preparedStatement.setString(1, status);
                preparedStatement.setString(2, assignment);
                preparedStatement.setString(3, crew);
                preparedStatement.setString(4, getTodaysDate()); 
                preparedStatement.setString(5, getCurrentUserName());
                preparedStatement.setString(6, driveName);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info("MySQL Update Success: " + driveName + " is now " + status);
                } else {
                    logger.warn("MySQL Update Failed: Drive not found (" + driveName + ")");
                }

            } catch (SQLException e) {
                logger.error("Failed to update drive in MySQL", e);
            }
        });
    }

    @Override
    public String getTodaysDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    
    
	// =================================================================
	// STUBS
	// =================================================================

	@Override
	public boolean doesCrewNameExist(String crewName) {
		return false;
	}

	@Override
	public boolean isTokenValid() {
		return true;
	}

	@Override
	public void fetchUserInfo() {
	}

	@Override
	public String getCurrentUserName() {
		return "Alex Admin Kron";
	}

	@Override
	public String getCurrentUserEmail() {
		return "akron@clearanglestudios.co.uk";
	}

	@Override
	public void logUserOut() {
	}

	@Override
	public void clearCurrentUser() {
	}

	@Override
	public List<String> getFilteredDriveNames(String status) {
		return new ArrayList<>();
	}

	@Override
	public List<String> getFilteredDriveNames(String status, String size) {
		return new ArrayList<>();
	}

	@Override
	public String getFilteredCrewName(String driveNameToMatch) {
		return "";
	}


	@Override
	public List<Crew> getCrewData() {
		return new ArrayList<>();
	}

	@Override
	public List<String> getPcNames() {
		return new ArrayList<>();
	}

	@Override
	public List<String> getITEmailAddresses() {
		return new ArrayList<>();
	}

	@Override
	public List<Key> getAllKeys() {
		return new ArrayList<>();
	}

	@Override
	public void queueKeyUpdate(Key key) {
	}

	@Override
	public void queueKeyReturn(Key key) {
	}

	@Override
	public List<String> getKeyLoggers() {
		return new ArrayList<>();
	}

	@Override
	public void clearCache() {
	}

	@Override
	public String getLastSyncTime() {
		return "Live DB";
	}

	@Override
	public void logGeneralSecurityException(String context, GeneralSecurityException e) {
		logger.error(context, e);
	}

	@Override
	public void logIOException(String context, IOException e) {
		logger.error(context, e);
	}
}