package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.CategoriesPage;
import com.OnTerminal.pages.LoginPage;
import com.OnTerminal.pages.PlantsPage;
import com.OnTerminal.pages.SalesPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

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

    // ==================== AUTHENTICATION STEPS ====================

    /**
     * Login as specified role (admin or user)
     */
    // @TC_SALES_ADM_UI_001
    @Given("user is logged in as {string}")
    public void userIsLoggedInAs(String role) {
        System.out.println("[CommonUiSteps] Logging in as: " + role);
        loginPage.loginAs(role);
        System.out.println("[CommonUiSteps] Login complete. Current URL: " + 
                          loginPage.getDriver().getCurrentUrl());
    }

    // ==================== NAVIGATION STEPS ====================

    /**
     * Navigate to Plants page
     */
    // @TC_SALES_ADM_UI_001
    @Given("user navigates to plants page")
    public void userNavigatesToPlantsPage() {
        System.out.println("[CommonUiSteps] Navigating to plants page...");
        plantsPage.openPlantsPage();
    }

    /**
     * Navigate to Sell Plant page
     */
    // @TC_SALES_ADM_UI_001
    @When("user navigates to sell plant page")
    public void userNavigatesToSellPlantPage() {
        System.out.println("[CommonUiSteps] Navigating to sell plant page...");
        salesPage.openSellPlantPage();
    }
}
