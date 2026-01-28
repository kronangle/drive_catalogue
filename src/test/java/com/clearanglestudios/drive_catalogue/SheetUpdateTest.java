package com.clearanglestudios.drive_catalogue;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clearanglestudios.objects.SheetUpdate;

public class SheetUpdateTest {

    @Test
    public void testSheetUpdate_StoresDataCorrectly() {
        // Arrange
        String[] expectedInfo = {"DriveA", "in", "CrewB", ""};
        
        // Act
        SheetUpdate ticket = new SheetUpdate(expectedInfo);
        
        // Assert
        assertArrayEquals(expectedInfo, ticket.getInfo());
    }
}

