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

    @When("user searches category by keyword {string}")
    public void user_searches_category_by_keyword(String keyword) {
        categoriesPage.searchByKeyword(keyword);
        System.out.println("Searched for keyword: " + keyword);
    }

    @Then("results should contain category name {string}")
    public void results_should_contain_category_name(String categoryName) {
        assertTrue("Results should contain category: " + categoryName,
                categoriesPage.containsCategoryName(categoryName));
    }

    @When("user filters by parent category {string}")
    public void user_filters_by_parent_category(String parentCategory) {
        categoriesPage.filterByParentCategory(parentCategory);
        System.out.println("Filtered by parent category: " + parentCategory);
    }

    @Then("results should show only children of {string}")
    public void results_should_show_only_children_of(String parentCategory) {
        assertTrue("Results should show only children of: " + parentCategory,
                categoriesPage.showsOnlyChildrenOf(parentCategory));
    }
}
