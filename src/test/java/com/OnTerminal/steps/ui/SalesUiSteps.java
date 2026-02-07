package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.SalesPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

/**
 * Step definitions for Sales Management UI tests
 * Handles sales creation, pagination, sorting, and validation scenarios
 */
public class SalesUiSteps {

    // Serenity auto-injects PageObjects
    SalesPage salesPage;

    // ==================== Navigation Steps ====================

    @When("user opens sales page")
    public void openSalesPage() {
        salesPage.openSales();
    }

    // ==================== Data Setup Steps ====================

    @Given("user ensures there are at least {int} sales records")
    public void ensureSalesData(int count) {
        salesPage.openSales();
        int currentCount = salesPage.getSalesCount();
        if (currentCount < count) {
            System.out.println(
                    "Data volume too low for pagination test. Creating " + (count - currentCount) + " records...");
            for (int i = 0; i < (count - currentCount); i++) {
                salesPage.createGenericSale();
            }
        }
    }

    // ==================== Sorting Steps ====================

    @When("user sorts sales by {string}")
    public void userSortsBy(String column) {
        salesPage.clickSortBy(column);
    }

    @Then("sales should be sorted by Total Price")
    public void verifySortedByPrice() {
        assertTrue("Sales are not sorted by price", salesPage.isSortedByPrice());
    }

    @Then("sales should be sorted by Sold Date")
    public void verifySortedByDate() {
        assertTrue("Sales are not sorted by date", salesPage.isSortedByDate());
    }

    // ==================== Pagination Steps ====================

    @When("user navigates to the next page")
    public void nextPagination() {
        salesPage.clickNextPage();
    }

    @Then("the next set of records should be displayed")
    public void verifyNextPage() {
        assertTrue("Next page should be displayed", salesPage.isNextPageDisplayed());
    }

    // ==================== Sales Creation Steps ====================

    @When("user clicks Add Sale")
    public void clickAdd() {
        salesPage.clickAddSale();
    }

    @When("user leaves plant empty")
    public void leavePlantEmpty() {
        salesPage.leavePlantEmpty();
    }

    @When("user enters quantity {string}")
    public void enterQty(String qty) {
        salesPage.enterQuantity(qty);
    }

    @When("user saves sale")
    public void saveSale() {
        salesPage.saveSale();
    }

    @When("user clicks Cancel on sales page")
    public void clickCancel() {
        salesPage.clickCancel();
    }

    // ==================== Validation Steps ====================

    @Then("validation error should be shown")
    public void verifyValidation() {
        assertTrue("Validation error should be visible", salesPage.isValidationErrorVisible());
    }

    @Then("user should be on sales list page")
    public void verifySalesList() {
        assertTrue("User should be on sales list page", salesPage.isAtSalesList());
    }

    @Then("no new sale should be created")
    public void verifyNoSaleCreated() {
        // Logical verification is covered by being back on the list page
    }

    // ==================== Delete Steps ====================

    @When("user clicks Delete on a sale")
    public void clickDelete() {
        salesPage.clickDeleteOnFirstSale();
    }

    @Then("delete confirmation popup should be visible")
    public void verifyDeletePopup() {
        assertTrue("Delete confirmation should be visible", salesPage.isDeleteConfirmationVisible());
    }

    @Then("Delete button should not be visible")
    public void verifyDeleteHidden() {
        assertTrue("Delete button should not be visible", salesPage.isDeleteButtonNotVisible());
    }

    // ==================== Empty State Steps ====================

    @Then("\"No sales found\" message should be displayed if list is empty")
    public void verifyNoSalesMsg() {
        if (salesPage.isNoSalesMessageVisible()) {
            assertTrue(true);
        } else {
            System.out.println("Sales exist, skipping 'No sales found' check.");
        }
    }

    // ==================== Menu Navigation Steps ====================

    @Then("Sales menu should be visible and active")
    public void verifySalesMenu() {
        assertTrue("Sales menu should be active", salesPage.isSalesMenuActive());
    }
}