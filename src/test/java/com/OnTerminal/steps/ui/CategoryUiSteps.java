package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.CategoriesPage;
import com.OnTerminal.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;

import static org.junit.Assert.assertTrue;

public class CategoryUiSteps {

    // Serenity injects PageObjects automatically
    @Steps
    LoginPage loginPage;
    @Steps
    CategoriesPage categoriesPage;

    @Given("user is logged in as {string}")
    public void user_is_logged_in_as(String role) {
        if (role.equalsIgnoreCase("admin")) {
            loginPage.login("admin", "admin123");
        } else {
            loginPage.login("user", "user123"); // change if your app uses different user creds
        }
    }

    @When("user opens categories page")
    public void user_opens_categories_page() {
        categoriesPage.openCategories();
    }

    @Then("categories table should be visible")
    public void categories_table_should_be_visible() {
        assertTrue("Categories table should be visible", categoriesPage.isTableVisible());
    }

    @Then("pagination controls should be visible")
    public void pagination_controls_should_be_visible() {
        assertTrue("Pagination controls should be visible", categoriesPage.isPaginationVisible());
    }
}
