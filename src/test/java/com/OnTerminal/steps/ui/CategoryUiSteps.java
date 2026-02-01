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
            loginPage.login("testuser", "test123");
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

    @When("user clicks Add Category")
    public void user_clicks_add_category() {
        categoriesPage.clickAddCategory();
        System.out.println("Clicked Add Category button");
    }

    @When("user enters category name {string}")
    public void user_enters_category_name(String categoryName) {
        categoriesPage.enterCategoryName(categoryName);
        System.out.println("Entered category name: " + categoryName);
    }

    @When("user saves category")
    public void user_saves_category() {
        categoriesPage.saveCategory();
        System.out.println("Saved category");
    }

    @Then("category {string} should appear in the list")
    public void category_should_appear_in_the_list(String categoryName) {
        assertTrue("Category should appear in the list: " + categoryName,
                categoriesPage.categoryAppearsInList(categoryName));
    }

    @Then("Add Category button should not be visible")
    public void add_category_button_should_not_be_visible() {
        assertTrue("Add Category button should NOT be visible for normal user",
                categoriesPage.isAddCategoryButtonNotVisible());
    }

    @When("user opens add category page directly")
    public void user_opens_add_category_page_directly() {
        categoriesPage.openAddCategoryPageDirectly();
        System.out.println("Attempted to access add category page directly");
    }

    @Then("user should see access denied page")
    public void user_should_see_access_denied_page() {
        assertTrue("User should see access denied page",
                categoriesPage.isAccessDeniedPage());
    }
}
