package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.CategoriesPage;
import com.OnTerminal.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;

import static org.junit.Assert.assertTrue;

public class CategoryUiSteps {

    @Steps
    LoginPage loginPage;

    @Steps
    CategoriesPage categoriesPage;

    @Given("user is logged in as admin")
    public void user_is_logged_in_as_admin() {
        // If locators differ in your app, update LoginPage.java
        loginPage.login("admin", "admin123");
    }

    @When("user opens categories page")
    public void user_opens_categories_page() {
        categoriesPage.openPage();
    }

    @Then("categories table should be visible")
    public void categories_table_should_be_visible() {
        assertTrue("Categories table should be visible", categoriesPage.isTableVisible());
    }
}
