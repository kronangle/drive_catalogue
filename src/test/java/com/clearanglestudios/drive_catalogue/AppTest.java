//package com.clearanglestudios.drive_catalogue;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.io.IOException;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.testfx.framework.junit.ApplicationTest;
//
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//public class AppTest extends ApplicationTest {
//
//	@Override
//	public void start(Stage stage) throws IOException {
////		Use the same instance of the App
//		new App().start(stage);
//	}
//
//	@Before
//	public void setUp() throws Exception {
////		App.isTestEnvironment = true;
//	}
//
//	@After
//	public void tearDown() throws Exception {
////		App.isTestEnvironment = false;
//	}
//
////	================================================================================
////	
////								Login Pane Tests
////	
////	================================================================================
//
//	@Test
//	public void primary_scene_should_not_be_null() {
//		assertThat(App.scene).isNotNull();
//	}
//
//	@Test
//	public void login_button_clickedOn_loads_primary_pane() throws IOException {
////	When:
//		clickOn("#loginButton_LoginPage");
//
//		sleep(5000);
//
////  Then:
//		VBox pane = lookup("#panePrimary").query();
//		assertThat(pane).isNotNull();
//	}
//
////	================================================================================
////	
////								Primary Pane Tests
////	
////	================================================================================
//
//	@Test
//	public void primary_assign_button_clickedOn_loads_drive_assign_pane() throws IOException {
////		When:
//		navigateToDriveAssignPane();
//
////      Then:
//		Pane pane = lookup("#paneDriveAssign").query();
//		assertThat(pane).isNotNull();
//	}
//
//	@Test
//	public void primary_ingest_button_clickedOn_loads_drive_ingest_pane() throws IOException {
////		When:
//		navigateToDriveIngestPane();
//
////      Then:
//		Pane pane = lookup("#paneDriveIngest").query();
//		assertThat(pane).isNotNull();
//	}
//
//	@Test
//	public void primary_return_button_clickedOn_loads_drive_return_pane() throws IOException {
////		When:
//		navigateToDriveReturnPane();
//
////      Then:
//		Pane pane = lookup("#paneDriveReturn").query();
//		assertThat(pane).isNotNull();
//	}
//
//	@Test
//	public void primary_query_button_clickedOn_loads_drive_query_pane() throws IOException {
////		When:
//		navigateToDriveQueryPane();
//
////      Then:
//		Pane pane = lookup("#paneDriveQuery").query();
//		assertThat(pane).isNotNull();
//	}
//
////	================================================================================
////	
////								Drive Assign Pane Tests
////	
////	================================================================================
//	
//	@Test
//	public void drive_assign_cancel_button_clickedOn_loads_primary_pane() throws IOException, InterruptedException {
////		When:
//		navigateToDriveAssignPane();
//		
//		clickOn("#cancelButton_DriveAssign");
//		sleep(2000);
//		
////      Then:
//		Pane pane = lookup("#panePrimary").query();
//		assertThat(pane).isNotNull();
//	}
//	
////	================================================================================
////	
////								Drive Ingest Pane Tests
////	
////	================================================================================
//	
//	@Test
//	public void drive_ingest_cancel_button_clickedOn_loads_primary_pane() throws IOException, InterruptedException {
////		When:
//		navigateToDriveIngestPane();
//		
//		clickOn("#cancelButton_DriveIngest");
//		sleep(2000);
//		
////      Then:
//		Pane pane = lookup("#panePrimary").query();
//		assertThat(pane).isNotNull();
//	}
//	
////	================================================================================
////	
////								Drive Return Pane Tests
////	
////	================================================================================
//	
//	@Test
//	public void drive_return_cancel_button_clickedOn_loads_primary_pane() throws IOException, InterruptedException {
////		When:
//		navigateToDriveReturnPane();
//		
//		clickOn("#cancelButton_DriveReturn");
//		sleep(2000);
//		
////      Then:
//		Pane pane = lookup("#panePrimary").query();
//		assertThat(pane).isNotNull();
//	}
//	
////	================================================================================
////	
////								Drive Query Pane Tests
////	
////	================================================================================
//	
//	@Test
//	public void drive_query_finish_button_clickedOn_loads_primary_pane() throws IOException, InterruptedException {
////		When:
//		navigateToDriveQueryPane();
//		
//		clickOn("#finishedButton_DriveQuery");
//		sleep(2000);
//		
////      Then:
//		Pane pane = lookup("#panePrimary").query();
//		assertThat(pane).isNotNull();
//	}
//
////================================================================================
////
////								Navigation methods
////
////================================================================================
//
//	private void navigateToPrimaryScene() {
//		clickOn("#loginButton_LoginPage");
//
//		sleep(5000);
//	}
//
//	private void navigateToDriveAssignPane() {
//		navigateToPrimaryScene();
//		clickOn("#assignButton_Primary");
//
//		sleep(5000);
//	}
//
//	private void navigateToDriveIngestPane() {
//		navigateToPrimaryScene();
//		clickOn("#ingestButton_Primary");
//
//		sleep(5000);
//	}
//
//	private void navigateToDriveReturnPane() {
//		navigateToPrimaryScene();
//		clickOn("#returnButton_Primary");
//
//		sleep(5000);
//	}
//
//	private void navigateToDriveQueryPane() {
//		navigateToPrimaryScene();
//		clickOn("#queryButton_Primary");
//
//		sleep(5000);
//	}
//
//}
