package com.OnTerminal.steps;

import com.OnTerminal.config.ConfigManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.core.Serenity;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber Hooks - Test Setup and Teardown
 * ==========================================
 * 
 * This class implements hooks that run before/after scenarios
 * to ensure a clean test environment.
 */
public class Hooks {

    private static final ConfigManager config = ConfigManager.getInstance();

    // ==================== Before Hooks ====================

    /**
     * Runs before EVERY scenario
     * Sets up the test environment
     */
    @Before(order = 1)
    public void setUp(Scenario scenario) {
        logScenarioStart(scenario);
    }

    /**
     * Runs before UI scenarios only
     * Prepares browser settings
     */
    @Before(value = "@ui", order = 2)
    public void setUpUi(Scenario scenario) {
        System.out.println("[Setup] Configuring UI test environment...");
        System.out.println("[Setup] Base URL: " + config.getBaseUrl());
        System.out.println("[Setup] Browser: " + config.getBrowser());
    }

    /**
     * Runs before API scenarios only
     * Clears any previous API state
     */
    @Before(value = "@api", order = 2)
    public void setUpApi(Scenario scenario) {
        System.out.println("[Setup] Configuring API test environment...");
        System.out.println("[Setup] API Base URL: " + config.getApiBaseUrl());
        // Clear any stored tokens from previous tests
        BaseApiSteps.authToken = null;
        BaseApiSteps.lastResponse = null;
    }

    /**
     * Runs before smoke tests
     * Smoke tests are critical paths
     */
    @Before(value = "@smoke", order = 3)
    public void setUpSmoke(Scenario scenario) {
        System.out.println("[Setup] This is a SMOKE test - Critical path validation");
    }

    /**
     * Runs before security tests
     * Security tests may need special handling
     */
    @Before(value = "@security", order = 3)
    public void setUpSecurity(Scenario scenario) {
        System.out.println("[Setup] This is a SECURITY test - Authentication/Authorization testing");
    }

    // ==================== After Hooks ====================

    /**
     * Runs after EVERY scenario
     * Captures screenshot on failure and logs results
     */
    @After(order = 1)
    public void tearDown(Scenario scenario) {
        logScenarioEnd(scenario);
        
        if (scenario.isFailed()) {
            captureScreenshotOnFailure(scenario);
        }
    }

    /**
     * Runs after UI scenarios
     * Cleans up browser state
     */
    @After(value = "@ui", order = 2)
    public void tearDownUi(Scenario scenario) {
        System.out.println("[Teardown] Cleaning up UI test state...");
        
        // Clear cookies and session storage
        try {
            WebDriver driver = Serenity.getDriver();
            if (driver != null) {
                driver.manage().deleteAllCookies();
                System.out.println("[Teardown] Cookies cleared");
            }
        } catch (Exception e) {
            System.out.println("[Teardown] Could not clear cookies: " + e.getMessage());
        }
    }

    /**
     * Runs after API scenarios
     * Cleans up API test data
     */
    @After(value = "@api", order = 2)
    public void tearDownApi(Scenario scenario) {
        System.out.println("[Teardown] Cleaning up API test state...");
        
        // Clean up any test data created during the test
        cleanUpTestData();
        
        // Clear stored state
        BaseApiSteps.authToken = null;
        BaseApiSteps.testData.clear();
    }

    /**
     * Runs after admin tests
     * Admin tests may create data that needs cleanup
     */
    @After(value = "@admin", order = 3)
    public void tearDownAdmin(Scenario scenario) {
        System.out.println("[Teardown] Admin test cleanup...");
        // Add specific cleanup for admin-created resources if needed
    }

    // ==================== Helper Methods ====================

    /**
     * Log scenario start with tags and name
     */
    private void logScenarioStart(Scenario scenario) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SCENARIO START: " + scenario.getName());
        System.out.println("Tags: " + scenario.getSourceTagNames());
        System.out.println("Source: " + scenario.getUri());
        System.out.println("=".repeat(80));
    }

    /**
     * Log scenario end with status
     */
    private void logScenarioEnd(Scenario scenario) {
        String status = scenario.isFailed() ? "FAILED ❌" : "PASSED ✓";
        System.out.println("\n" + "-".repeat(80));
        System.out.println("SCENARIO END: " + scenario.getName());
        System.out.println("Status: " + status);
        System.out.println("-".repeat(80) + "\n");
    }

    /**
     * Capture screenshot on failure
     */
    private void captureScreenshotOnFailure(Scenario scenario) {
        try {
            WebDriver driver = Serenity.getDriver();
            if (driver != null && driver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot on Failure");
                System.out.println("[Teardown] Screenshot captured for failed scenario");
            }
        } catch (Exception e) {
            System.out.println("[Teardown] Could not capture screenshot: " + e.getMessage());
        }
    }

    /**
     * Clean up test data created during API tests
     */
    private void cleanUpTestData() {
        // Get IDs of resources created during test
        Integer createdCategoryId = BaseApiSteps.testData.get("createdCategoryId") instanceof Integer 
            ? (Integer) BaseApiSteps.testData.get("createdCategoryId") : null;
        Integer createdPlantId = BaseApiSteps.testData.get("createdPlantId") instanceof Integer 
            ? (Integer) BaseApiSteps.testData.get("createdPlantId") : null;

        // Clean up created categories
        if (createdCategoryId != null) {
            try {
                System.out.println("[Cleanup] Deleting test category ID: " + createdCategoryId);
                // Note: In a real implementation, you would call the delete API here
                // This is a placeholder for the cleanup logic
            } catch (Exception e) {
                System.out.println("[Cleanup] Could not delete category: " + e.getMessage());
            }
        }

        // Clean up created plants
        if (createdPlantId != null) {
            try {
                System.out.println("[Cleanup] Deleting test plant ID: " + createdPlantId);
                // Note: In a real implementation, you would call the delete API here
            } catch (Exception e) {
                System.out.println("[Cleanup] Could not delete plant: " + e.getMessage());
            }
        }
    }
}
