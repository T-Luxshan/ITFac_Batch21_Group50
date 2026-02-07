package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.DashboardPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class InventoryUiSteps {

    DashboardPage dashboardPage;

    @When("user attempts to click {string} menu item")
    public void userClicksMenuItem(String menuItem) {
        if (menuItem.equalsIgnoreCase("Inventory")) {
            dashboardPage.clickInventory();
        } else {
            throw new IllegalArgumentException("Menu item '" + menuItem + "' is not supported in this step definition");
        }
    }

    @Then("the Inventory page should be displayed")
    public void verifyInventoryPageDisplayed() {
        Assert.assertTrue("Inventory page should be displayed",
                dashboardPage.isInventoryPageDisplayed());
    }
}
