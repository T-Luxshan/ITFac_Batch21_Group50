package com.OnTerminal.steps.ui;

import com.OnTerminal.config.Constants;
import com.OnTerminal.pages.CategoriesPage;
import com.OnTerminal.pages.LoginPage;
import com.OnTerminal.pages.PlantsPage;
import com.OnTerminal.pages.SalesPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.Assert.*;

import org.openqa.selenium.WebElement;

/**
 * CommonUiSteps - Shared UI Step Definitions
 * ==========================================
 */
public class CommonUiSteps {

    // Page Objects - automatically injected by Serenity
    LoginPage loginPage;
    SalesPage salesPage;
    PlantsPage plantsPage;
    CategoriesPage categoriesPage;
    
    // ==================== CONFIGURATION ====================
    private final com.OnTerminal.config.ConfigManager config =
            com.OnTerminal.config.ConfigManager.getInstance();

    /**
     * Login as specified role (admin or user)
     */
    @Given("user is logged in as {string}")
    public void userIsLoggedInAs(String role) {
        System.out.println("[CommonUiSteps] Logging in as: " + role);
        loginPage.loginAs(role);
        System.out.println("[CommonUiSteps] Login complete. Current URL: " +
                loginPage.getDriver().getCurrentUrl());
    }

    /**
     * Clear user session - ensure not logged in
     */
    @Given("user is not logged in")
    public void userIsNotLoggedIn() {
        System.out.println("[CommonUiSteps] Clearing user session...");
        try {
            loginPage.getDriver().get(config.getLogoutUrl());
            sleep(Constants.Timeouts.SHORT_WAIT * 1000);
        } catch (Exception e) {
            // If logout fails, clear cookies
            loginPage.getDriver().manage().deleteAllCookies();
        }
        System.out.println("[CommonUiSteps] User session cleared - not logged in");
    }

    // ==================== NAVIGATION STEPS ====================

    /**
     * Navigate to Plants page
     */
    @Given("user navigates to plants page")
    public void userNavigatesToPlantsPage() {
        System.out.println("[CommonUiSteps] Navigating to plants page...");
        plantsPage.openPlantsPage();
    }

    /**
     * Navigate to Sell Plant page
     */
    @When("user navigates to sell plant page")
    public void userNavigatesToSellPlantPage() {
        System.out.println("[CommonUiSteps] Navigating to sell plant page...");
        salesPage.openSellPlantPage();
    }

    /**
     * Navigate directly to categories page (bypass navigation)
     */
    @When("user navigates directly to categories page")
    public void userNavigatesDirectlyToCategoriesPage() {
        System.out.println("[CommonUiSteps] Navigating DIRECTLY to categories page (URL access)...");
        loginPage.getDriver().get(config.getCategoriesUrl());
        sleep(Constants.Timeouts.SHORT_WAIT * 1000);
    }

    /**
     * Navigate to Sales page
     * Used by: Sales UI tests
     * 
     * Gherkin: When user navigates to sales page
     */
    @When("user navigates to sales page")
    public void userNavigatesToSalesPage() {
        System.out.println("[CommonUiSteps] Navigating to sales page...");
        salesPage.openSalesPage();
    }

    /**
     * Navigate directly to plant edit page
     */
    @When("user navigates directly to plant edit page with id {string}")
    public void userNavigatesDirectlyToPlantEdit(String plantId) {
        System.out.println("[CommonUiSteps] Navigating DIRECTLY to plant edit page ID: " + plantId);
        plantsPage.openEditPlantPage(plantId);
    }

    /**
     * Navigate to Categories page (alternative wording)
     */
    @When("user navigates to categories page")
    public void userNavigatesToCategoriesPage() {
        System.out.println("[CommonUiSteps] Navigating to categories page...");
        categoriesPage.openCategories();
    }

    /**
     * Verify logout success message is displayed
     */
    @Then("user should see logout success message")
    public void userShouldSeeLogoutSuccessMessage() {
        WebElement logoutSuccessAlert = loginPage.getDriver().findElement(
                org.openqa.selenium.By.cssSelector("div.alert.alert-success.text-center"));
        String alertText = logoutSuccessAlert.getText().toLowerCase();
        boolean hasSuccessMessage = alertText.contains("logged out successfully");
        System.out
                .println("[CommonUiSteps] Logout success alert found: " + hasSuccessMessage + " | Text: " + alertText);
        assertTrue("Logout success message should be displayed", hasSuccessMessage);
    }
    
    /**
     * Verify user is redirected to login page
     */
    @Then("user should be redirected to login page")
    public void userShouldBeRedirectedToLoginPage() {
        String currentUrl = loginPage.getDriver().getCurrentUrl();
        System.out.println("[CommonUiSteps] Checking redirect to login. Current URL: " + currentUrl);
        assertTrue("User should be redirected to login page",
                  currentUrl.contains(Constants.UrlPaths.UI_LOGIN) || 
                  currentUrl.contains("/login"));
    }
    // ==================== VERIFICATION STEPS ====================
    // Common assertions used across multiple feature files

    /**
     * Verify Categories menu item is highlighted/active
     */
    @Then("Categories menu item should be highlighted")
    public void categoriesMenuItemShouldBeHighlighted() {
        System.out.println("[CommonUiSteps] Verifying Categories menu item is highlighted...");
        categoriesPage.verifyActiveMenuItem("categories");
    }

    /**
     * Verify Plants menu item is highlighted/active
     */
    @Then("Plants menu item should be highlighted")
    public void plantsMenuItemShouldBeHighlighted() {
        System.out.println("[CommonUiSteps] Verifying Plants menu item is highlighted...");
        // Using categoriesPage.verifyActiveMenuItem - centralized in CategoriesPage
        categoriesPage.verifyActiveMenuItem("plants");
    }

    /**
     * Verify Sales menu item is highlighted/active
     */
    @Then("Sales menu item should be highlighted")
    public void salesMenuItemShouldBeHighlighted() {
        System.out.println("[CommonUiSteps] Verifying Sales menu item is highlighted...");
        // Using categoriesPage.verifyActiveMenuItem - centralized in CategoriesPage
        categoriesPage.verifyActiveMenuItem("sales");
    }


    /**
     * Verify access denied page is displayed
     */
    @Then("user should see access denied page")
    public void userShouldSeeAccessDeniedPage() {
        System.out.println("[CommonUiSteps] Checking for access denied page...");

        // Check multiple indicators of access denied
        String currentUrl = loginPage.getDriver().getCurrentUrl();
        String pageSource = loginPage.getDriver().getPageSource().toLowerCase();
        String pageTitle = loginPage.getDriver().getTitle().toLowerCase();

        boolean isAccessDenied = currentUrl.contains("access-denied") ||
                currentUrl.contains("forbidden") ||
                pageSource.contains("403 - access denied") ||
                pageSource.contains("you do not have permission to access this page") ||
                pageTitle.contains("access denied") ||
                pageTitle.contains("forbidden");

        System.out.println("[CommonUiSteps] Access denied detected: " + isAccessDenied);
        assertTrue("User should see access denied page", isAccessDenied);
    }

    /**
     * Click the logout button
     * Used by: Authentication tests (TC_AUTH_USR_UI_010)
     * 
     * Gherkin: When user clicks logout button
     */
    @When("user clicks logout button")
    public void userClicksLogoutButton() {
        System.out.println("[CommonUiSteps] Attempting to click logout button...");
        try {
            WebElement logoutBtn = loginPage.getDriver().findElement(
                    org.openqa.selenium.By.cssSelector("a.nav-link.text-danger[href='/ui/logout']")
            );
            logoutBtn.click();
            sleep(Constants.Timeouts.SHORT_WAIT * 1000);
            System.out.println("[CommonUiSteps] Clicked logout button");
        } catch (Exception e) {
            // Fallback: navigate to logout URL directly
            System.out.println("[CommonUiSteps] Logout button not found, navigating to logout URL");
            loginPage.getDriver().get(config.getLogoutUrl());
            sleep(Constants.Timeouts.SHORT_WAIT * 1000);
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Helper method for waiting (thread sleep)
     * @param millis milliseconds to wait
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
