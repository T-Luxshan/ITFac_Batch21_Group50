package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.CategoriesPage;
import com.OnTerminal.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;

public class CategoryUiSteps {

    // PageObjects are injected automatically by Serenity
    LoginPage loginPage;
    CategoriesPage categoriesPage;

    @Given("user is logged in as {string}")
    public void user_is_logged_in_as(String role) {
        if (role.equalsIgnoreCase("admin")) {
            loginPage.login("admin", "admin123");
        } else {
            loginPage.login("user", "user123"); // change if needed
        }
    }

    @When("user opens categories page")
    public void user_opens_categories_page() {
        categoriesPage.openCategories();
        System.out.println("Opened URL: " + categoriesPage.getDriver().getCurrentUrl());
    }

    @Then("categories table should be visible")
    public void categories_table_should_be_visible() {
        assertTrue("Categories table/list should be visible", categoriesPage.isTableVisible());
    }

    @Then("pagination controls should be visible")
    public void pagination_controls_should_be_visible() {
        assertTrue("Pagination controls should be visible", categoriesPage.isPaginationVisible());
    }
}
