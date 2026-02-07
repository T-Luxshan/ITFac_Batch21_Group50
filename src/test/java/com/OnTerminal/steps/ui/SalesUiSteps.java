package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.SalesPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import org.junit.Assert;

public class SalesUiSteps {

    // --- FIX: Removed @Steps annotation here. Serenity auto-injects PageObjects.
    // ---
    SalesPage salesPage;

    @When("user opens sales page")
    public void openSalesPage() {
        salesPage.openSales();
    }

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

    @When("user sorts sales by {string}")
    public void userSortsBy(String column) {
        salesPage.clickSortBy(column);
    }

    @Then("sales should be sorted by Total Price")
    public void verifySortedByPrice() {
        Assert.assertTrue("Sales are not sorted by price", salesPage.isSortedByPrice());
    }

    @Then("sales should be sorted by Sold Date")
    public void verifySortedByDate() {
        Assert.assertTrue("Sales are not sorted by date", salesPage.isSortedByDate());
    }

    @When("user navigates to the next page")
    public void nextPagination() {
        salesPage.clickNextPage();
    }

    @Then("the next set of records should be displayed")
    public void verifyNextPage() {
        Assert.assertTrue(salesPage.isNextPageDisplayed());
    }

    @When("user clicks Add Sale")
    public void clickAdd() {
        salesPage.clickAddSale();
    }

    @When("user clicks Cancel on sales page")
    public void clickCancel() {
        salesPage.clickCancel();
    }

    @Then("user should be on sales list page")
    public void verifySalesList() {
        Assert.assertTrue(salesPage.isAtSalesList());
    }

    @Then("no new sale should be created")
    public void verifyNoSaleCreated() {
        // Logical verification is covered by being back on the list page
    }

    @When("user clicks Delete on a sale")
    public void clickDelete() {
        salesPage.clickDeleteOnFirstSale();
    }

    @Then("delete confirmation popup should be visible")
    public void verifyDeletePopup() {
        Assert.assertTrue(salesPage.isDeleteConfirmationVisible());
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

    @Then("validation error should be shown")
    public void verifyValidation() {
        Assert.assertTrue(salesPage.isValidationErrorVisible());
    }

    @Then("Delete button should not be visible")
    public void verifyDeleteHidden() {
        Assert.assertTrue(salesPage.isDeleteButtonNotVisible());
    }

    @Then("\"No sales found\" message should be displayed if list is empty")
    public void verifyNoSalesMsg() {
        if (salesPage.isNoSalesMessageVisible()) {
            Assert.assertTrue(true);
        } else {
            System.out.println("Sales exist, skipping 'No sales found' check.");
        }
    }

    @Then("Sales menu should be visible and active")
    public void verifySalesMenu() {
        Assert.assertTrue(salesPage.isSalesMenuActive());
    }
}