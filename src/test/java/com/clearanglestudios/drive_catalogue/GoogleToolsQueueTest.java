package com.clearanglestudios.drive_catalogue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.clearanglestudios.googleService.GoogleTools;
import com.clearanglestudios.objects.SheetUpdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class GoogleToolsQueueTest {

    private List<String> executionLog;

    @Before
    public void setUp() {
        executionLog = Collections.synchronizedList(new ArrayList<>());

        GoogleTools.setQueueProcessor((info) -> {
            executionLog.add("Processed: " + Arrays.toString(info));
        });
    }

    @After
    public void tearDown() {
        GoogleTools.setQueueProcessor((info) -> {
            try {
                GoogleTools.pushChangesToSheet(info); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testQueueMaintainsFIFOOrder() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3); // Expect 3 tasks to complete

        // Dependency injection
        GoogleTools.setQueueProcessor((info) -> {
            executionLog.add(Arrays.toString(info)); 
            latch.countDown(); // Count down after each task
        });

        // 3 Queue tasks 
        GoogleTools.queueSheetUpdate(new SheetUpdate(new String[]{"Task 1"}));
        GoogleTools.queueSheetUpdate(new SheetUpdate(new String[]{"Task 2"}));
        GoogleTools.queueSheetUpdate(new SheetUpdate(new String[]{"Task 3"}));

        // Wait up to 2 sec, then check if the count is 0
        boolean completed = latch.await(2, TimeUnit.SECONDS);

        // Confirm thread completed tasks
        assertThat(completed).as("Queue should process all items").isTrue();

        // Confirm FIFO
        assertThat(executionLog).containsExactly(
            "[Task 1]", 
            "[Task 2]", 
            "[Task 3]"
        );
    }
}