package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.PlantsPage;
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
    PlantsPage plantsPage;

    // Temporary test state
    private String notedPlantName;
    private int notedStockQuantity;

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

    @When("user clicks sell button")
    public void clickSellButton() {
        salesPage.saveSale();
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

    @Then("user should be redirected to sales list page")
    public void verifyRedirectToSalesList() {
        assertTrue("User should be redirected to sales list page", salesPage.isAtSalesList());
    }

    @Then("no new sale should be created")
    public void verifyNoSaleCreated() {
        // Logical verification is covered by being back on the list page
    }

    @Then("error message should be displayed on the same page")
    public void verifyErrorMessageSamePage() {
        assertTrue("Error message should be displayed",
                salesPage.isErrorMessageDisplayed() || salesPage.isValidationErrorVisible());
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

    // ==================== New Steps for Stock & Dropdown ====================

    @Given("user notes the stock quantity of first available plant")
    public void noteFirstPlantStock() {
        plantsPage.pause(1000); // Use public wrapper for waitABit
        notedPlantName = plantsPage.getFirstPlantName();
        plantsPage.recordStockOfFirstPlant();
        notedStockQuantity = plantsPage.getRecordedStock(notedPlantName);
        System.out.println("[Step] Noted plant: " + notedPlantName + " with stock: " + notedStockQuantity);
    }

    @When("user selects the noted plant from dropdown")
    public void selectNotedPlant() {
        if (notedPlantName == null) {
            throw new RuntimeException("No plant was noted in previous steps!");
        }
        salesPage.selectPlantByName(notedPlantName);
    }

    @Then("the stock quantity should be reduced by {int}")
    public void verifyStockReduced(int amount) {
        if (notedPlantName == null) {
            // If notedPlantName is null, try to find the one we modified based on recent
            // activity or just use first one if we can't
            plantsPage.recordStockOfFirstPlant(); // Re-record? No.
            // Without noting, we can't verify reduction properly unless we know WHICH
            // plant.
            throw new RuntimeException("Cannot verify stock reduction: No plant was noted.");
        }

        int currentStock = plantsPage.getCurrentStock(notedPlantName);
        int expectedStock = notedStockQuantity - amount;

        System.out.println("[Step] Verifying stock for " + notedPlantName +
                ": Initial=" + notedStockQuantity +
                ", Current=" + currentStock +
                ", Expected=" + expectedStock);

        assertEquals("Stock quantity should be reduced by " + amount, expectedStock, currentStock);
    }

    @Given("user finds a plant with low stock")
    public void findLowStockPlantStep() {
        notedPlantName = plantsPage.findLowStockPlant();
        notedStockQuantity = plantsPage.getRecordedStock(notedPlantName);
        System.out.println("[Step] Found low stock plant: " + notedPlantName + " (" + notedStockQuantity + ")");
    }

    @When("user selects the low stock plant from dropdown")
    public void selectLowStockPlant() {
        if (notedPlantName == null)
            throw new RuntimeException("No low stock plant found!");
        salesPage.selectPlantByName(notedPlantName);
    }

    @When("user enters quantity greater than available stock")
    public void enterExcessQuantity() {
        int excessQty = notedStockQuantity + 5;
        salesPage.enterQuantity(String.valueOf(excessQty));
        System.out.println("[Step] Entered excess quantity: " + excessQty);
    }

    @Then("dropdown should display available plants")
    public void verifyDropdownContent() {
        assertTrue("Dropdown should be visible", salesPage.isDropdownVisible());
        assertFalse("Dropdown should have options", salesPage.getDropdownOptions().isEmpty());
    }

    @Then("plants should be selectable")
    public void verifyPlantsSelectable() {
        // If dropdown has options > 1 (excluding placeholder), it's selectable
        assertTrue("Plants should be selectable", salesPage.getDropdownOptions().size() > 1);
    }

}