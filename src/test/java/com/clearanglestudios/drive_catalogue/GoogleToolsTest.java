package com.clearanglestudios.drive_catalogue;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.clearanglestudios.googleService.GoogleTools;
import com.clearanglestudios.objects.Crew;
import com.clearanglestudios.objects.Drive;

public class GoogleToolsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void checkIfNameIsConvertedToEmailPrefixCorrectly() {
		String nameToTest = GoogleTools.getEmailPrefix("Joe Bloggs");
		assertEquals("jbloggs", nameToTest);
	}

	@Test
	public void checkIfDoubleBarrelNameIsConvertedToEmailPrefixCorrectly() {
		String nameToTest = GoogleTools.getEmailPrefix("Joe Bloggs-Smith");
		assertEquals("jbloggssmith", nameToTest);
	}

	@Test
	public void checkIfShortNameIsConvertedToEmailPrefixCorrectly() {
		String nameToTest = GoogleTools.getEmailPrefix("jbloggs");
		assertEquals("jbloggs", nameToTest);
	}

	@Test
	public void checkGetDriveDataReturnsArrayListOfDrive() throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(drives).isNotNull();
		assertThat(drives instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : drives) {
			assertThat(item instanceof Drive);
		}
	}

	@Test
	public void checkGetCrewDataReturnsArrayListOfCrew() throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Crew> crew = GoogleTools.getCrewData();

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(crew).isNotNull();
		assertThat(crew instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : crew) {
			assertThat(item instanceof Drive);
		}
	}

	@Test
	public void checkGetPCDataReturnsArrayListOfStrings() throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<String> pcData = GoogleTools.getPcNames();

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(pcData).isNotNull();
		assertThat(pcData instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : pcData) {
			assertThat(item instanceof String);
		}

		// Check first PC name is alphabetically sorted
		assertThat(pcData.get(0).equals("Akuma"));
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfIn()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("in");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("in")) {
				assertThat(driveNames.contains(drive.getName()));
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfOut()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("out");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("out")) {
				assertThat(driveNames.contains(drive.getName()));
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfIngesting()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("ingesting");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("ingesting")) {
				assertThat(driveNames.contains(drive.getName()));
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfInSizeOf1TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("in", "1TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("in")) {
				if (drive.getSize().equalsIgnoreCase("1TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfInSizeOf2TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("in", "2TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("in")) {
				if (drive.getSize().equalsIgnoreCase("2TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfInSizeOf4TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("in", "4TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("in")) {
				if (drive.getSize().equalsIgnoreCase("4TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfInSizeOf8TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("in", "8TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("in")) {
				if (drive.getSize().equalsIgnoreCase("8TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfOutSizeOf1TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("out", "1TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("out")) {
				if (drive.getSize().equalsIgnoreCase("1TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfOutSizeOf2TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("out", "2TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("out")) {
				if (drive.getSize().equalsIgnoreCase("2TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfOutSizeOf4TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("out", "4TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("out")) {
				if (drive.getSize().equalsIgnoreCase("4TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfOutSizeOf8TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("out", "8TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("out")) {
				if (drive.getSize().equalsIgnoreCase("8TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfIngestingSizeOf1TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("ingesting", "1TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("ingesting")) {
				if (drive.getSize().equalsIgnoreCase("1TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfIngestingSizeOf2TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("ingesting", "2TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("ingesting")) {
				if (drive.getSize().equalsIgnoreCase("2TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfIngestingSizeOf4TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("ingesting", "4TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("ingesting")) {
				if (drive.getSize().equalsIgnoreCase("4TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredDriveDataReturnsArrayListOfDriveWithStatusOfIngestingSizeOf8TB()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		ArrayList<String> driveNames = GoogleTools.getFilteredDriveNames("ingesting", "8TB");

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(driveNames).isNotNull();
		assertThat(driveNames instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : driveNames) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		for (Drive drive : drives) {
			if (drive.getStatus().equalsIgnoreCase("ingesting")) {
				if (drive.getSize().equalsIgnoreCase("8TB")) {
					assertThat(driveNames.contains(drive.getName()));
				}
			}
		}
	}

	@Test
	public void checkGetFilteredCrewNameReturnsCrewNameStringMatchingGivenDriveName()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		String driveName = drives.get(0).getName();
		String crewName = drives.get(0).getCrew();

		String crewNameToCheck = GoogleTools.getFilteredCrewName(driveName);

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(crewNameToCheck).isNotNull();
		assertThat(crewNameToCheck instanceof String);

		// Check for correct output
		assertThat(crewName.equalsIgnoreCase(crewNameToCheck));
	}

	@Test
	public void checkGetITEmailAddressesReturnsArrayListOfStringThatIncludesAnITEmailAddress()
			throws GeneralSecurityException, IOException {
		// When: call the method
		ArrayList<String> itEmailAddresses = GoogleTools.getITEmailAddresses();
		String emailToCheck = "akron@clearanglestudios.co.uk";

		// Then: Verify the returned value is not null and an ArrayList
		assertThat(itEmailAddresses).isNotNull();
		assertThat(itEmailAddresses instanceof ArrayList);

		// Check that the ArrayList contains Drive objects
		for (Object item : itEmailAddresses) {
			assertThat(item instanceof String);
		}

		// Check that returned list is accurate
		assertThat(itEmailAddresses.contains(emailToCheck));

	}

	@Test
	public void checkDoesDriveNameExistGoodValue() throws GeneralSecurityException, IOException {
		ArrayList<Drive> drives = GoogleTools.getDriveData();
		String driveNameToCompare = drives.get(0).getName();

		assertTrue(GoogleTools.doesDriveNameExist(driveNameToCompare));
	}

	@Test
	public void checkDoesDriveNameExistBadValue() throws GeneralSecurityException, IOException {
		String driveNameToCompare = "bad value for name";

		assertFalse(GoogleTools.doesDriveNameExist(driveNameToCompare));
	}

	@Test
	public void checkDoesCreweNameExistGoodValue() throws GeneralSecurityException, IOException {
		ArrayList<Crew> crew = GoogleTools.getCrewData();
		String crewNameToCompare = crew.get(0).getName();

		assertTrue(GoogleTools.doesCrewNameExist(crewNameToCompare));
	}

	@Test
	public void checkDoesCrewNameExistBadValue() throws GeneralSecurityException, IOException {
		String crewNameToCompare = "bad value for name";

		assertFalse(GoogleTools.doesCrewNameExist(crewNameToCompare));
	}

	@Test
	public void checkAreDetailsGoodGoodValue() throws GeneralSecurityException, IOException {
		String[] detailsToCompare = { "", "", "", "Some detail" };

		assertTrue(GoogleTools.areTheDetailsGood(detailsToCompare));
	}

	@Test
	public void checkAreDetailsGoodBadValue() throws GeneralSecurityException, IOException {
		String[] detailsToCompare = { "", "", "", "" };

		assertFalse(GoogleTools.areTheDetailsGood(detailsToCompare));
	}
	
	@Test
	public void checkPCNameExistGoodValue() throws GeneralSecurityException, IOException {
		ArrayList<String> pcList = GoogleTools.getPcNames();
		String pcName = pcList.get(0);
		String[] pcNameToCompare = { "", "", "", pcName };
		
		assertTrue(GoogleTools.doesPCNameExist(pcNameToCompare));
	}
	
	@Test
	public void checkPCNameExistBadValue() throws GeneralSecurityException, IOException {
		String[] pcNameToCompare = { "", "", "", "Bad PC Name" };
		
		assertFalse(GoogleTools.doesPCNameExist(pcNameToCompare));
	}

	@Test
    public void testIsTokenValid_WhenLoggedOut_ReturnsFalse() {
        // 1. Arrange: Ensure user is logged out
        GoogleTools.logUserOut(); 

        // 2. Act: Ask if the token is valid
        boolean isValid = GoogleTools.isTokenValid(); 

        // 3. Assert: Use AssertJ syntax
        assertThat(isValid)
            .as("Token should be invalid when logged out")
            .isFalse();
    }
}
