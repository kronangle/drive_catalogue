package com.clearanglestudios.googleService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearanglestudios.drive_catalogue.App;
import com.clearanglestudios.drive_catalogue.LoginPageController;
import com.clearanglestudios.objects.Crew;
import com.clearanglestudios.objects.Drive;
import com.clearanglestudios.objects.SheetUpdate;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GoogleTools {

//	=====================================================================================
//	
//										DECLARATIONS
//	
//	=====================================================================================

//	Google spreadsheet constant values
	private static final String SPREADSHEET_ID = "1DrFLxok8YfehhbIxcbreBVV-kN__MIzf9BFJQEP7d_o"; // Which sheet
//	private static final String ACTIVE_SHEET = "drive_catalogue";
	private static final String ACTIVE_SHEET = "drive_catalogue_test";
	private static final String DRIVE_DATA_SHEET_AND_RANGE = ACTIVE_SHEET + "!A8:K170"; // where to get drive data
	private static final String UPDATE_RANGE = ACTIVE_SHEET + "!A";
	private static final String CREW_DATA_SHEET_AND_RANGE = "data_reference!A2:D70"; // where to get crew data
	private static final String PC_DATA_SHEET_AND_RANGE = "data_reference!G2:G100"; // where to get pc data
	private static final String IT_EMAIL_DATA_SHEET_AND_RANGE = "data_reference!J2:J100"; // where to get iT data

//	Google constant values/
	private static final String APPLICATION_NAME = "CAS Drive Catalogue";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final List<String> SCOPES = Arrays.asList(PeopleServiceScopes.USERINFO_PROFILE,
			SheetsScopes.SPREADSHEETS, GmailScopes.GMAIL_SEND, PeopleServiceScopes.USERINFO_EMAIL);

//	File path to client ID json 
	private static final String CREDENTIALS_FILE_PATH = "/com/clearanglestudios/important_files/client_secret.json";

//	Statuses to be included in "all"
	private static final Set<String> ALLOWED_STATUSES = Set.of("in", "out", "ingesting");

//	Option controls
	private static final String DEFAULT = "All";
	private static final String NO_RECORD_PLACEHOLDER = "no record";

//	Alert Email
	private static final String EMAIL_ALERT_SUBJECT = "Drive | %s | %s | %s";
	private static final String EMAIL_ALERT_BODY = """
			Hi %s,

			The status of a hard drive assigned to your name has changed.

			%-11s	 %-15s
			%-10s	%-15s
			%-11s	 %-15s
			%-11s	%-15s

			For further information, please contact your line manager.

			Kind regards,
			%s
			""";
	private static final String COMPANY_EMAIL_SUFFIX = "@clearanglestudios.co.uk";
	private static final String EMAIL_LABEL_DRIVE = "Drive";
	private static final String EMAIL_LABEL_STATUS = "Status";
	private static final String EMAIL_LABEL_DATE = "Date";
	private static final String EMAIL_LABEL_DETAILS = "Details";

//	Cache Storage
	private static Map<String, List<List<Object>>> dataCache = new HashMap<>();
	private static LocalDateTime lastSyncTime = null;

//	-------------------------------------------------------------------------------------

//	Initialise Logger
	private static final Logger logger = LogManager.getLogger(GoogleTools.class);
//	Initialise ExecutorService with 1 Thread
	private static final ExecutorService updateQueue = Executors.newSingleThreadExecutor();

//	User session data
	private static Person currentUser;

//	=====================================================================================
//	
//									GOOGLE METHODS
//	
//	=====================================================================================

//	  ------ Google API service requests -------------------------------

//	Return an authenticated Sheets service
	public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
//      Build a new authorized API client service
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Credential credential = getCredentials(HTTP_TRANSPORT);

		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
				.build();
	}

//  Return an authenticated People service  
	public static PeopleService getPeopleService() throws GeneralSecurityException, IOException {
//      Build a new authorized API client service
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

		Credential credential = getCredentials(HTTP_TRANSPORT);

		return new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
				.build();
	}

//	Return an authenticated Mail service
	public static Gmail getGmailService() throws GeneralSecurityException, IOException {
//      Build a new authorized API client service
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

		Credential credential = getCredentials(HTTP_TRANSPORT);

		return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

//	---------------------------- Credential Requester -----------------------------------

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
//	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
////	  --------------------------------------
////		Load client secrets.
//		InputStream in = GoogleTools.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//		if (in == null) {
//			logger.error("Resource not found: " + CREDENTIALS_FILE_PATH);
//			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//		}
//		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
////    --------------------------------------
////		Build flow and trigger user authorization request.
//		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
//				clientSecrets, SCOPES)
//				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//				.setAccessType("offline").build();
////    --------------------------------------
////		Set the receiver
////		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(0).build();
////		logger.info("Getting free port...");
////		int freePort = getFreePort();
////		logger.info("Using free port: " + freePort);
//		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(/*freePort*/-1).build();
//		logger.info("Created LocalServerReceiver at URI: " + receiver.getRedirectUri());
////    --------------------------------------
//		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
////    --------------------------------------
//	}

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
//		  --------------------------------------
//			Load client secrets.
		InputStream in = GoogleTools.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			logger.error("Resource not found: " + CREDENTIALS_FILE_PATH);
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		//     --------------------------------------
//			Build flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();
		//     --------------------------------------
//			Set the receiver
//	      FIX 1: Force IPv4 (127.0.0.1) and Port -1 (Auto).
//	      FIX 2: Do NOT call getRedirectUri() here. It starts the server too early.
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost("127.0.0.1").setPort(-1).build();

		logger.info("Created LocalServerReceiver (Auto-Port). Handing off to Authorization App.");
		//     --------------------------------------
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	private static int getFreePort() {
		try (ServerSocket socket = new ServerSocket(0)) {
			socket.setReuseAddress(true);
			return socket.getLocalPort();
		} catch (IOException e) {
			throw new RuntimeException("Failed to find a free port", e);
		}
	}

//	--------------------------- Spreadsheet Retriever -----------------------------------

//	Method for setting up the Google API service and returns a List of Lists generated from the spreadsheet.
	public static List<List<Object>> readValuesFromSheet(String range, int total_columns)
			throws GeneralSecurityException, IOException {
//		--------------------------------------
//		 	Check the cache
		if (dataCache.containsKey(range)) {
			logger.info("YES CACHE: Returning data for range: " + range);
			return dataCache.get(range);
		}
		logger.info("NO CACHE: Fetching fresh data from Google for range: " + range);
//		--------------------------------------
//		    Get an authenticated Sheets service to work with
		Sheets service = getSheetsService();
//		    Use the service and convert the spreadsheet to values
		ValueRange response = service.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
//		--------------------------------------
//		    Spreadsheet values as a List of Lists 
		List<List<Object>> values = response.getValues();
		List<List<Object>> valuesClean = new ArrayList<>();

//		    Clean data from spreadsheet
		for (List<Object> row : values) {
//			Create a new list with a fixed size of TOTAL_COLUMNS
			List<Object> paddedRow = new ArrayList<>(Collections.nCopies(total_columns, NO_RECORD_PLACEHOLDER));

//			Copy existing values into the correct positions
			for (int i = 0; i < row.size() && i < total_columns; i++) {
				paddedRow.set(i, row.get(i));
			}

//			Check value for empty string
			for (int i = 0; i < row.size() && i < total_columns; i++) {
				Object value = row.get(i);
//				If the value is an empty string, leave it as "No record"
				paddedRow.set(i, (value == null || value.toString().trim().isEmpty()) ? NO_RECORD_PLACEHOLDER : value);
			}

			valuesClean.add(paddedRow);
		}
//		--------------------------------------
//		Store values in cache Map
		dataCache.put(range, valuesClean);
		lastSyncTime = LocalDateTime.now();
//		--------------------------------------
		return valuesClean;
	}

//	=================================================================
//	
//							 USER SESSION DATA
//	
//	=================================================================

//	---------------------------- User Logout Method -------------------------------------

//	Allows the user to log out of their account
	public static void logUserOut() {
		File tokenFile = new File(TOKENS_DIRECTORY_PATH + "/StoredCredential");

		if (tokenFile.exists()) {
			if (tokenFile.delete()) {
				logger.info("User logged out. Token file deleted.");
			} else {
				logger.warn("Failed to delete token file.");
			}
		} else {
			logger.warn("No token file found to delete.");
		}
	}

//	--------------------------- Start up user checks ------------------------------------

	/**
	 * Checks if the stored credentials are valid by attempting a lightweight API
	 * call.
	 * 
	 * @return true if the token works, false if missing or expired.
	 */
	public static boolean isTokenValid() {
		File tokenFile = new File(TOKENS_DIRECTORY_PATH + "/StoredCredential");
		if (!tokenFile.exists()) {
			return false;
		}

		try {
			Sheets service = getSheetsService();
			service.spreadsheets().get(SPREADSHEET_ID).setFields("properties").execute();

			logger.info("Token validation successful. User is logged in.");
			return true;

		} catch (Exception e) {
			logger.warn("Token validation failed. Credential might be expired.", e);
			// If the token is bad, delete it
			logUserOut();
			return false;
		}
	}

	/**
     * Fetches the user's profile data (Name, Email) from Google 
     * and stores it in memory.
     */
    public static void fetchUserInfo() throws IOException, GeneralSecurityException {
        PeopleService peopleService = getPeopleService();

        currentUser = peopleService.people().get("people/me")
                .setPersonFields("names,emailAddresses")
                .execute();
                
        logger.info("User info fetched successfully.");
    }

    /**
     * Returns the full name of the logged-in user.
     * Safe to call from any controller (returns "Unknown" if null).
     */
    public static String getCurrentUserName() {
        if (currentUser != null && !currentUser.getNames().isEmpty()) {
            return currentUser.getNames().get(0).getDisplayName();
        }
        return "Unknown User";
    }

    /**
     * Returns the email of the logged-in user.
     */
    public static String getCurrentUserEmail() {
        if (currentUser != null && !currentUser.getEmailAddresses().isEmpty()) {
            return currentUser.getEmailAddresses().get(0).getValue();
        }
        return "unknown@email.com";
    }
    
    public static void clearCurrentUser() {
		currentUser = null;
		logger.info("Current user cleared");
	}

	
//	=====================================================================================
//	
//								DATA MANAGEMENT METHODS
//	
//	=====================================================================================	

//	Returns an ArrayList of Drive objects with their states filled with data pulled from the spreadsheet
	public static ArrayList<Drive> getDriveData() throws GeneralSecurityException, IOException {
		ArrayList<Drive> drives = new ArrayList<>();
//			----------------------------------------------------------
//			Get the drive data
		logger.info("Getting Drive Data from sheet...");
		List<List<Object>> driveData = GoogleTools.readValuesFromSheet(DRIVE_DATA_SHEET_AND_RANGE, 10);
		logger.info("Got Drive Data from sheet...");
//		----------------------------------------------------------
//			Cycle through each row of the drive data			
		for (List<Object> row : driveData) {
//				Extract the necessary data and create List of Drive objects
			Drive drive = new Drive((String) row.get(0), (String) row.get(1), (String) row.get(2), (String) row.get(3),
					(String) row.get(4), (String) row.get(5), (String) row.get(6), (String) row.get(7),
					(String) row.get(8), (String) row.get(9));
			drives.add(drive);
		}
//		----------------------------------------------------------
		return drives;
	}

//	Returns an ArrayList of Crew objects with their states filled with data pulled from the spreadsheet	
	public static ArrayList<Crew> getCrewData() throws GeneralSecurityException, IOException {
		ArrayList<Crew> crewList = new ArrayList<>();
//			----------------------------------------------------------
//			Get the crew data
		logger.info("Getting Crew Data from sheet...");
		List<List<Object>> crewData = GoogleTools.readValuesFromSheet(CREW_DATA_SHEET_AND_RANGE, 4);
		logger.info("Got Crew Data from sheet...");
//		----------------------------------------------------------
//			Cycle through each row of the crew data			
		for (List<Object> row : crewData) {
//				Extract the necessary data and create List of Crew objects
			Crew crew = new Crew((String) row.get(0) + " " + (String) row.get(1), (String) row.get(2),
					(String) row.get(3));
			crewList.add(crew);
		}
//		----------------------------------------------------------
		return crewList;
	}

//	Return an ArrayList of Strings with PC names for drive ingesting
	public static ArrayList<String> getPcNames() throws GeneralSecurityException, IOException {
		ArrayList<String> pcNames = new ArrayList<>();
//		----------------------------------------------------------
//		Get the PC data
		logger.info("Getting PC Data from sheet...");
		List<List<Object>> pcData = GoogleTools.readValuesFromSheet(PC_DATA_SHEET_AND_RANGE, 1);
		logger.info("Got PC Data from sheet...");
//		----------------------------------------------------------
//		Cycle through each row of the PC data			
		for (List<Object> row : pcData) {
//			Extract the necessary data and create List of Strings
			String pcName = (String) row.get(0);
			pcNames.add(pcName);
		}
		Collections.sort(pcNames);
//		----------------------------------------------------------
		return pcNames;
	}

//	Return an ArrayList of drive names that meet the status and size requirement
	public static ArrayList<String> getFilteredDriveNames(String status, String size)
			throws GeneralSecurityException, IOException {
		ArrayList<Drive> drives = getDriveData(); // Drive data
		ArrayList<String> driveNames = new ArrayList<>(); // Final product of Strings
//		---------------------------------------
		for (Drive drive : drives) {
//			---------------------------------------
			String driveStatus = drive.getStatus().trim().toLowerCase();
			String cleanSize = size.trim().toLowerCase();
			String driveSize = drive.getSize().toLowerCase().substring(0, 1);
			boolean shouldProcessDrive = false;
//			---------------------------------------
//			Check if drive should be processed base on status				
			if (status.toLowerCase().equalsIgnoreCase(DEFAULT) && ALLOWED_STATUSES.contains(driveStatus)) {
				shouldProcessDrive = true;
			} else if (driveStatus.equals(status)) {
				shouldProcessDrive = true;
			}
//			---------------------------------------
//			If drive should be processed
			if (shouldProcessDrive) {
//				Then evaluate the size element
				if (cleanSize.equalsIgnoreCase(DEFAULT)) {
					driveNames.add(drive.getName());
				} else {
					if (driveSize.equals(size.substring(0, 1).toLowerCase())) {
						driveNames.add(drive.getName());
					}
				}
			}
//			---------------------------------------	
		}
		Collections.sort(driveNames);
		logger.info("Filtered drive names: " + driveNames);
		return driveNames;
	}

//	Return an ArrayList of drive names that meet the status requirement
	public static ArrayList<String> getFilteredDriveNames(String status) throws GeneralSecurityException, IOException {
		ArrayList<Drive> drives = getDriveData();
		ArrayList<String> driveNames = new ArrayList<>();

		for (Drive drive : drives) {
			if (drive.getStatus().trim().toLowerCase().equals(status)) {
				driveNames.add(drive.getName());
			}
		}
		Collections.sort(driveNames);

		return driveNames;
	}

//	Return the crew name that matches the drive name
	public static String getFilteredCrewName(String driveNameToMatch) throws GeneralSecurityException, IOException {
		ArrayList<Drive> drives = getDriveData();

		if (driveNameToMatch != null) {
			for (Drive drive : drives) {
				if (drive.getName().toLowerCase().trim().equals(driveNameToMatch.toLowerCase().trim())) {
					return drive.getCrew();
				}
			}
		}
		return null;
	}

//	Return an ArrayList of Strings with email address allowed to use drive ingesting
	public static ArrayList<String> getITEmailAddresses() throws GeneralSecurityException, IOException {
		ArrayList<String> itEmails = new ArrayList<>();
//			----------------------------------------------------------
//			Get the PC data
		logger.info("Getting IT Data from sheet...");
		List<List<Object>> itData = GoogleTools.readValuesFromSheet(IT_EMAIL_DATA_SHEET_AND_RANGE, 1);
		logger.info("Got IT Data from sheet...");

//			Cycle through each row of the PC data			
		for (List<Object> row : itData) {
//				Extract the necessary data and create List of Strings
			String itEmail = (String) row.get(0);
			itEmails.add(itEmail);
		}

		return itEmails;
	}

//	Clears the local memory cache.
	public static void clearCache() {
		logger.info("Clearing local data cache.");
		dataCache.clear();
	}

//	Return the time that the cache was last synced to the Google Sheet
	public static String getLastSyncTime() {
		if (lastSyncTime == null)
			return "Never";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		return lastSyncTime.format(formatter);
	}

	/**
	 * Adds an update task to the background queue. This returns IMMEDIATELY,
	 * allowing the UI to stay responsive.
	 */
	public static void queueSheetUpdate(SheetUpdate action) {
		logger.info("Queuing update task...");

		// Submit the task to the background thread
		updateQueue.submit(() -> {
			try {
				logger.info("Processing queue item...");
				pushChangesToSheet(action.getInfo());
			} catch (Exception e) {
				logger.error("Background Queue Failed", e);
			}
		});
	}

//	=====================================================================================
//	
//								EMAIL MANAGEMENT METHODS
//	
//	=====================================================================================

	/**
	 * Create a MimeMessage using the parameters provided.
	 *
	 * @param toEmailAddress   email address of the receiver
	 * @param fromEmailAddress email address of the sender, the mailbox account
	 * @param subject          subject of the email
	 * @param bodyText         body text of the email
	 * @return the MimeMessage to be used to send email
	 * @throws MessagingException - if a wrongly formatted address is encountered.
	 */
	public static MimeMessage createEmail(String toEmailAddress, String fromEmailAddress, String subject,
			String bodyText) throws MessagingException {
//		----------------------------------------
//		Create a MimeMessage
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);
//	    ----------------------------------------
//		Set the values of the email
		email.setFrom(new InternetAddress(fromEmailAddress));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toEmailAddress));
		email.setSubject(subject);
		email.setText(bodyText);
//	    ----------------------------------------
//	    Return the MimeMessage object
		return email;
	}

	/**
	 * Create a message from an email.
	 *
	 * @param emailContent Email to be set to raw of message
	 * @return a message containing a base64url encoded email
	 * @throws IOException        - if service account credentials file not found.
	 * @throws MessagingException - if a wrongly formatted address is encountered.
	 */
	public static Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
//		----------------------------------------
//		Read content of the MimeMessage email
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		emailContent.writeTo(buffer);
//	    ----------------------------------------
//	    Use read content to encode the message
		byte[] bytes = buffer.toByteArray();
		String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
//	    ----------------------------------------
//	    Create the Message object using the encoded content
		Message message = new Message();
		message.setRaw(encodedEmail);
//	    ----------------------------------------
//	    Return the encoded Message object
		return message;
	}

	/**
	 * Send an email from the user's mailbox to its recipient.
	 *
	 * @param fromEmailAddress - Email address to appear in the from: header
	 * @param toEmailAddress   - Email address of the recipient
	 * @return the sent message, {@code null} otherwise.
	 * @throws MessagingException       - if a wrongly formatted address is
	 *                                  encountered.
	 * @throws IOException              - if service account credentials file not
	 *                                  found.
	 * @throws GeneralSecurityException
	 */
	public static void sendEmail(String crewToEmail, String driveName, String status, String date, String loggerPerson,
			String details) throws MessagingException, IOException, GeneralSecurityException {
//	    ----------------------------------------
//		Get authenticated Gmail service
		Gmail service = getGmailService();
//	    ----------------------------------------
//		Declare parameters for the email
		String toEmailAddress = getEmailPrefix(crewToEmail) + COMPANY_EMAIL_SUFFIX;
		String fromEmailAddress = getCurrentUserEmail();
		String subject = String.format(EMAIL_ALERT_SUBJECT, driveName, status, date);
		String bodyText = String.format(EMAIL_ALERT_BODY, crewToEmail, EMAIL_LABEL_DRIVE, driveName, EMAIL_LABEL_STATUS,
				status, EMAIL_LABEL_DATE, date, EMAIL_LABEL_DETAILS, details, loggerPerson);
//	    ----------------------------------------
//		Create the email content 
		MimeMessage email = createEmail(toEmailAddress, fromEmailAddress, subject, bodyText);
		Message emailMessage = createMessageWithEmail(email);
//	    ----------------------------------------
//		Send message
		try {
			emailMessage = service.users().messages().send("me", emailMessage).execute();
//		    ----------------------------------------
			logger.info("Email Sent!");
			logger.info("Message id: " + emailMessage.getId());
			logger.info(emailMessage.toPrettyString());
			App.showNotification("Email sent!");
//		    ----------------------------------------
		} catch (GoogleJsonResponseException e) {
			GoogleJsonError error = e.getDetails();
			if (error.getCode() == 403) {
				logger.warn("Unable to send email");
				logger.error("GoogleJsonResponseException: error code = 403 ", e);
				App.showNotification("Unable to send Email - " + e.getMessage());
			} else {
				logger.warn("Unable to send email");
				logger.error("GoogleJsonResponseException: ", e);
				App.showNotification("Unable to send Email - " + e.getMessage());
				throw e;
			}
		}
	}

//	Returns the crew name that has been parsed or the crew name from the target row
	private static String getCrewToEmail(String crewNew, String crewOld) {
//		Check if new crew value is empty
		if (crewNew == "") {
//			Grab the old crew name value from target row
			return crewOld;
		} else {
//			Grab the new crew name from info
			return crewNew;
		}
	}

	/**
	 * Removes any '-' from double barrel names. Checks the crew name if its 1 or 2
	 * words. If its 2 words, then it finds the position of the ' ' and separates
	 * the last name. It adds the initial of the first name plus the surname.
	 * Otherwise, it returns the name as is.
	 * 
	 * @param crewToEmail - Taken from the target row
	 * @return email prefix based off crew name
	 */
	public static String getEmailPrefix(String crewToEmail) {
//		Get rid of double barrel surnames
		crewToEmail = crewToEmail.toLowerCase().trim().replace("-", "");
//		Check if the name gathered is 1 or 2 words
		int locationOfSpace = crewToEmail.indexOf(' ');
//		If a space is found
		if (locationOfSpace != -1) {
//			Grab the surname
			String lastName = crewToEmail.substring(locationOfSpace + 1);
//			Return first letter + last name
			return crewToEmail.substring(0, 1) + lastName;
		} else {
			return crewToEmail;
		}
	}

//	=====================================================================================
//	
//								DATA VERIFICATION METHODS
//	
//	=====================================================================================

//	Determine which tests are required and return results as a Map
	public static Map<String, Boolean> verifyInfo(String[] info) throws GeneralSecurityException, IOException {
		switch (info[1]) {
		case "in": // Returning a drive
			return verifyReturnData(info);
		case "out": // Assigning a drive
			return verifyAssignData(info);
		case "ingesting": // Ingesting a drive
			return verifyIngestingData(info);
		default:
			logger.warn("Unexpected value in verifyInfo: " + info[1]);
			throw new IllegalArgumentException("Unexpected value: " + info[1]);
		}
	}

//	Returns results from check DriveAssign fields
	private static Map<String, Boolean> verifyAssignData(String[] info) throws GeneralSecurityException, IOException {
		Map<String, Boolean> verifyResults = new HashMap<>();
		verifyResults.put("DriveName", doesDriveNameExist(info));
		verifyResults.put("CrewName", doesCrewNameExist(info));
		verifyResults.put("Details", areTheDetailsGood(info));
		return verifyResults;
	}

//	Returns results from check DriveIngest fields
	private static Map<String, Boolean> verifyIngestingData(String[] info)
			throws GeneralSecurityException, IOException {
		Map<String, Boolean> verifyResults = new HashMap<>();
		verifyResults.put("DriveName", doesDriveNameExist(info));
		verifyResults.put("PcName", doesPCNameExist(info));
		return verifyResults;
	}

//	Returns results from check DriveReturn fields
	private static Map<String, Boolean> verifyReturnData(String[] info) throws GeneralSecurityException, IOException {
		Map<String, Boolean> verifyResults = new HashMap<>();
		verifyResults.put("DriveName", doesDriveNameExist(info));
		return verifyResults;
	}

	/**
	 * Checks if the details meet criteria
	 * 
	 * @param info
	 * @return
	 */
	public static boolean areTheDetailsGood(String[] info) {
		// Check details
		String details = info[3];
		boolean detailsAreGood = false;
		if (details != null) {
			if (!details.isEmpty()) {
				if (!details.isBlank()) {
					logger.info("Details is not: null | empty | blank");
					detailsAreGood = true;
				} else {
					logger.warn("Details is not: null | empty  but it is blank");
				}
			} else {
				logger.warn("Details is not: null but it is empty");
			}
		} else {
			logger.warn("Details is null");
		}
		return detailsAreGood;
	}

	/**
	 * Checks is the crew name exists on the data reference
	 * 
	 * @param info
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private static boolean doesCrewNameExist(String[] info) throws GeneralSecurityException, IOException {
		// Check crew name
		ArrayList<Crew> crewList = getCrewData();
		String crewName = info[2];
		boolean foundCrew = false;
		for (Crew crew : crewList) {
			if (crew.getName().equalsIgnoreCase(crewName)) {
				foundCrew = true;
				break;
			}
		}
		return foundCrew;
	}

	/**
	 * Checks is the crew name exists on the data reference
	 * 
	 * @param crewName
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static boolean doesCrewNameExist(String crewName) throws GeneralSecurityException, IOException {
//		Check crew name
		ArrayList<Crew> crewList = getCrewData();
		boolean foundCrew = false;
		for (Crew crew : crewList) {
			if (crew.getName().equalsIgnoreCase(crewName)) {
				foundCrew = true;
				break;
			}
		}
		return foundCrew;
	}

	/**
	 * Checks is the drive name exists on the data reference
	 * 
	 * @param info
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private static boolean doesDriveNameExist(String[] info) throws GeneralSecurityException, IOException {
//		Check drive name exists on the sheet
		ArrayList<Drive> driveList = getDriveData();
		String driveName = info[0];

		for (Drive drive : driveList) {
			if (drive.getName().equalsIgnoreCase(driveName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks is the crew name exists on the data reference
	 * 
	 * @param driveName
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static boolean doesDriveNameExist(String driveName) throws GeneralSecurityException, IOException {
//		Check drive name exists on the sheet
		ArrayList<Drive> driveList = getDriveData();

		for (Drive drive : driveList) {
			if (drive.getName().equalsIgnoreCase(driveName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks is the pc name exists on the data reference
	 * 
	 * @param info
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static boolean doesPCNameExist(String[] info) throws GeneralSecurityException, IOException {
		// Check PC name
		ArrayList<String> pcList = getPcNames();
		String pcName = info[3];

		if (pcList.contains(pcName)) {
			return true;
		}
		return false;
	}

//	=====================================================================================
//	
//								SPREADSHEET METHODS
//	
//	=====================================================================================

//	Make the changes on the spreadsheet
	public static void pushChangesToSheet(String[] info) throws GeneralSecurityException, IOException {
//		----------------------------------------
//		Get the drive data
		logger.info("Getting Drive Data from sheet...");
		List<List<Object>> driveData = GoogleTools.readValuesFromSheet(DRIVE_DATA_SHEET_AND_RANGE, 10);
		logger.info("Got Drive data from sheet...");
//		----------------------------------------
//		Find the row with the matching hard drive name (column 2)
		int targetRowIndex = findTargetRow(info, driveData);
//		----------------------------------------
//      Quality check
		if (targetRowIndex == -1) {
			logger.warn("Hard drive name not found in the spreadsheet when pushingChangesToSheet");
			App.showNotification("Hard drive name not found in the spreadsheet.");
			return;
		}
//		----------------------------------------
//      Create a List object from the matching row to update the values in the corresponding columns
		List<Object> targetRow = driveData.get(targetRowIndex);
//		----------------------------------------
//		Get today's date
		String today = getTodaysDate();
//		----------------------------------------
//      Get the user's name
		String loggersName = getCurrentUserName();
//		----------------------------------------
//		Get crew name from target row if crew value will get wiped
		String crewToEmail = getCrewToEmail(info[2], (String) targetRow.get(5));
//		----------------------------------------
//		Update specific columns
		targetRow.set(3, info[1]); // Status (column 4)
		targetRow.set(4, info[3]); // Details/PC name (column 5)
		targetRow.set(5, info[2]); // Crew (column 6)
		targetRow.set(6, today); // Date of entry (column 7)
		targetRow.set(7, loggersName); // Name of user making changes (column 8)
//		----------------------------------------
//		Write the updated row back to the spreadsheet
		commitChangesToSheet(targetRowIndex, targetRow);
//		----------------------------------------
//		Forget stale data in cache
		clearCache();
//		----------------------------------------
//		Send email to involved parties
		try {
			sendEmail(crewToEmail, info[0], info[1], today, loggersName, info[3]);
		} catch (MessagingException e) {
			logger.warn("Failed to send email");
			logger.error("MessagingException ", e);
			App.showNotification("Failed to send email");
		} catch (IOException e) {
			logger.warn("Failed to send email");
			logger.error("IOException ", e);
			App.showNotification("Failed to send email");
		} catch (GeneralSecurityException e) {
			logger.warn("Failed to send email");
			logger.error("GeneralSecurityException ", e);
			App.showNotification("Failed to send email");
		}
//		----------------------------------------
		logger.info("Row updated successfully: " + targetRow);
	}

	/**
	 * Commits the new values for a row on the spreadsheet.
	 * 
	 * @param targetRowIndex
	 * @param targetRow
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private static void commitChangesToSheet(int targetRowIndex, List<Object> targetRow)
			throws IOException, GeneralSecurityException {
		System.out.println("TargetRowIndex: " + targetRowIndex);
		System.out.println("TargetRowIndex + 8: " + (targetRowIndex + 8));
		System.out.println(DRIVE_DATA_SHEET_AND_RANGE);
//		Use dynamic range to limit it to only target row
		String updateRange = UPDATE_RANGE + (targetRowIndex + 8);

// 		Create a ValueRange object using the new target row values
		ValueRange body = new ValueRange().setValues(List.of(targetRow));

// 		Request an authenticated sheet service to use
		Sheets service = getSheetsService();

// 		Commit the new values to the sheet within the specified range
		service.spreadsheets().values().update(SPREADSHEET_ID, updateRange, body).setValueInputOption("RAW").execute();
	}

	/**
	 * Find the row that contains a matching hard drive name
	 * 
	 * @param info      gathered from the form including user inputs
	 * @param driveData gathered from the spreadsheet
	 * @return the number at which the target row resides
	 */
	private static int findTargetRow(String[] info, List<List<Object>> driveData) {
		int targetRowIndex = -1; // Used as a reference to the correct row

		for (int i = 0; i < driveData.size(); i++) {
			List<Object> row = driveData.get(i);
			if (row.size() > 1 && info[0].equals(((String) row.get(1)).toLowerCase().trim())) { // Match hard drive name
				targetRowIndex = i;
				break;
			}
		}
		return targetRowIndex;
	}

	/**
	 * Returns today's date as a String in dd-MM-yyyy format.
	 * 
	 * @return Today's date
	 */
	public static String getTodaysDate() {
// 		----------------------------------------
// 		Get today's date
		LocalDate today = LocalDate.now();
// 		Format the date as a string
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String formattedDate = today.format(formatter);
		return formattedDate;
//		----------------------------------------
	}

//	=====================================================================================
//	
//									ERROR METHODS
//	
//	=====================================================================================

//	Log and communicate Google Error GeneralSecurityException
	public static void logGeneralSecurityException(String clue, GeneralSecurityException e) {
		logger.warn(String.format("Failed to get %s data", clue));
		logger.error("GeneralSecurityException ", e);
		App.showNotification(String.format("Failed to get %s data", clue));
	}

//	Log and communicate Google Error IOException
	public static void logIOException(String clue, IOException e) {
		logger.warn(String.format("Failed to get %s data", clue));
		logger.error("IOException ", e);
		App.showNotification(String.format("Failed to get %s data", clue));
	}

	
}
