package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.PlantsPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

/**
 * Step Definitions for Plants UI Tests
 * Handles plant management operations like add, edit, cancel
 */
public class PlantsUiSteps {

    // Page Objects - automatically injected by Serenity
    PlantsPage plantsPage;

    // Test state
    private String testPlantName;

    // ==================== When Steps ====================

    @When("user clicks Add Plant button")
    public void userClicksAddPlantButton() {
        System.out.println("[PlantsUiSteps] Clicking Add Plant button...");
        plantsPage.clickAddPlantButton();
    }

    @And("user enters plant name {string}")
    public void userEntersPlantName(String plantName) {
        testPlantName = plantName;
        System.out.println("[PlantsUiSteps] Entering plant name: " + plantName);
        plantsPage.enterPlantName(plantName);
    }

    @And("user enters plant price {string}")
    public void userEntersPlantPrice(String price) {
        System.out.println("[PlantsUiSteps] Entering plant price: " + price);
        plantsPage.enterPlantPrice(price);
    }

    @And("user clicks cancel button")
    public void userClicksCancelButton() {
        System.out.println("[PlantsUiSteps] Clicking Cancel button...");
        plantsPage.clickCancelButton();
    }

    // ==================== Then Steps ====================

    @Then("user should be redirected to plants list page")
    public void userShouldBeRedirectedToPlantsListPage() {
        assertTrue("User should be on plants list page", 
                   plantsPage.isOnPlantsListPage());
        System.out.println("[PlantsUiSteps] Successfully redirected to plants list page");
    }

    @Then("new plant should not be created")
    public void newPlantShouldNotBeCreated() {
        assertFalse("Plant '" + testPlantName + "' should NOT exist in the list",
                    plantsPage.isPlantInList(testPlantName));
        System.out.println("[PlantsUiSteps] Verified plant '" + testPlantName + "' was NOT created");
    }
}
