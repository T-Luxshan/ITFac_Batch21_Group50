package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.CategoriesPage;
import com.OnTerminal.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.thucydides.model.util.EnvironmentVariables;

import static org.junit.Assert.assertTrue;

public class CategoryUiSteps {

    // PageObjects are injected automatically by Serenity
    LoginPage loginPage;
    CategoriesPage categoriesPage;
    private EnvironmentVariables environmentVariables;

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

    @When("user sorts by {string}")
    public void user_sorts_by(String columnName) {
        categoriesPage.sortBy(columnName);
    }

    @Then("categories should be sorted by ID")
    public void categories_should_be_sorted_by_id() {
        assertTrue("Categories should be sorted by ID",
                categoriesPage.isSortedByID());
    }

    @Then("categories should be sorted by Name")
    public void categories_should_be_sorted_by_name() {
        assertTrue("Categories should be sorted by Name",
                categoriesPage.isSortedByName());
    }

    @Then("categories should be grouped by parent category")
    public void categories_should_be_grouped_by_parent_category() {
        assertTrue("Categories should be grouped by parent category",
                categoriesPage.isGroupedByParent());
    }

    @When("user opens add category page")
    public void user_opens_add_category_page() {
        categoriesPage.clickAddCategory();
    }

    @When("user leaves parent category empty")
    public void user_leaves_parent_category_empty() {
        categoriesPage.leaveParentCategoryEmpty();
    }

    @Then("category {string} should be a main category")
    public void category_should_be_a_main_category(String categoryName) {
        assertTrue("Category should be a main category: " + categoryName,
                categoriesPage.isMainCategory(categoryName));
    }

    @When("user clicks Cancel")
    public void user_clicks_cancel() {
        categoriesPage.clickCancel();
    }

    @Then("user should be on categories list page")
    public void user_should_be_on_categories_list_page() {
        assertTrue("User should be on categories list page",
                categoriesPage.isAtCategoriesList());
    }

    @Then("validation error should be shown on categories page")
    public void verifyCatValidationError() {
        assertTrue("Validation error should be visible on categories page",
                categoriesPage.isValidationErrorVisible());
    }

}
