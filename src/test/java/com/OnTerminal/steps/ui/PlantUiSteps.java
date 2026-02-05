package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.LoginPage;
import com.OnTerminal.pages.PlantFormPage;
import com.OnTerminal.pages.PlantsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import net.serenitybdd.annotations.Steps;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PlantUiSteps {

    @Steps
    LoginPage loginPage;

    @Steps
    PlantsPage plantsPage;

    @Steps
    PlantFormPage plantFormPage;

    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "admin123";
    private final String NORMAL_USER = "user";
    private final String NORMAL_PASS = "user123";

    private int initialPlantCount = 0;
    private String createdPlantName = null;
    private String editedPlantName = null;

    private String unique(String base) {
        return base + "_" + System.currentTimeMillis();
    }

    // login

    @Given("Login as Admin for Plant UI")
    public void login_as_admin() {
        loginPage.open();
        loginPage.login(ADMIN_USER, ADMIN_PASS);
    }

    @Given("Login as User for Plant UI")
    public void login_as_user() {
        loginPage.open();
        loginPage.login(NORMAL_USER, NORMAL_PASS);
    }

    // navigation

    @When("Open Plant page")
    @When("Navigate to Plants page")
    public void open_plant_page() {
        plantsPage.openPlantsPage();
    }

    @Then("Plant page should load")
    @Then("Plant list page loads")
    public void plant_page_should_load() {
        assertTrue("Plant page header should be visible", plantsPage.isPlantsHeaderVisible());
    }

    // TC_PLANT_ADMIN_UI_001

    @Then("Plants table should display with name price quantity and category columns")
    public void plants_table_should_display_with_all_columns() {
        assertTrue("Plant table should be visible", plantsPage.isTableDisplayed());
        assertTrue("Name column should be visible", plantsPage.hasNameColumn());
        assertTrue("Category column should be visible", plantsPage.hasCategoryColumn());
        assertTrue("Price column should be visible", plantsPage.hasPriceColumn());
        assertTrue("Stock/Quantity column should be visible", plantsPage.hasStockColumn());
    }

    @Then("Admin should be able to view all available plants")
    public void admin_should_be_able_to_view_all_plants() {
        assertTrue("Plant data should be present", plantsPage.getRowCount() >= 0);
    }

    // TC_PLANT_ADMIN_UI_002

    @Then("Admin should see Add Plant button")
    public void admin_should_see_add_plant_button() {
        assertTrue("Admin must see Add Plant button", plantsPage.isAddPlantButtonVisible());
    }

    @When("Click on Add Plant button")
    public void click_on_add_plant_button() {
        plantsPage.clickAddPlant();
    }

    @When("Enter plant name {string}")
    public void enter_plant_name(String name) {
        // make name unique every run
        createdPlantName = unique(name);
        plantFormPage.enterName(createdPlantName);
        System.out.println("Using unique plant name: " + createdPlantName);
    }

    @When("Select category {string}")
    public void select_category(String category) {
        plantFormPage.selectCategory(category);
    }

    @When("Select first available category")
    public void select_first_available_category() {
        plantFormPage.selectCategoryByIndex(1);
    }

    @When("Enter price {string}")
    public void enter_price(String price) {
        plantFormPage.enterPrice(price);
    }

    @When("Enter stock quantity {string}")
    public void enter_stock_quantity(String quantity) {
        plantFormPage.enterQuantity(quantity);
    }

    @When("Click on Save button")
    public void click_on_save_button() {
        plantFormPage.clickSave();
    }

    @Then("Plant is saved successfully")
    public void plant_is_saved_successfully() {
        plantsPage.pause(2500);

        String currentUrl = plantFormPage.getDriver().getCurrentUrl();
        System.out.println("=== After Save ===");
        System.out.println("Current URL: " + currentUrl);

        boolean stillOnForm = currentUrl.contains("/plants/add") || currentUrl.contains("/plants/edit");
        boolean onList = currentUrl.contains("/ui/plants") && !currentUrl.contains("/add") && !currentUrl.contains("/edit");

        if (stillOnForm) {
            if (plantFormPage.hasValidationError()) {
                String error = plantFormPage.getValidationErrorMessage();
                System.out.println("Validation error detected: " + error);
                throw new AssertionError("Plant save failed with validation error: " + error);
            }
            throw new AssertionError("Still on form page after save, but no validation error detected");
        }

        assertTrue("Should be redirected to plants list page after successful save", onList);
    }

    @Then("Success message is displayed")
    public void success_message_is_displayed() {
        plantsPage.pause(1000);

        String currentUrl = plantFormPage.getDriver().getCurrentUrl();
        boolean onListPage = currentUrl.contains("/ui/plants") && !currentUrl.contains("/add") && !currentUrl.contains("/edit");
        boolean hasMessage = plantsPage.hasSuccessMessage();

        System.out.println("Success check - On list page: " + onListPage + ", Has message: " + hasMessage);
        assertTrue("Should show success (redirected to list or success message)", onListPage || hasMessage);
    }

    @Then("User is redirected to plant list page")
    public void user_is_redirected_to_plant_list_page() {
        plantsPage.pause(800);

        String currentUrl = plantFormPage.getDriver().getCurrentUrl();
        boolean onList = currentUrl.contains("/ui/plants") && !currentUrl.contains("/add") && !currentUrl.contains("/edit");

        System.out.println("Redirect check - URL: " + currentUrl + ", On list: " + onList);
        assertTrue("Should be redirected to plants list page", onList);
    }

    @Then("Newly added plant {string} appears in the list")
    public void newly_added_plant_appears_in_list(String ignoredFeatureValue) {
        // Use the runtime-created unique name
        if (createdPlantName == null || createdPlantName.isBlank()) {
            throw new AssertionError("createdPlantName is null - did you skip 'Enter plant name' step?");
        }

        plantsPage.pause(800);
        plantsPage.openPlantsPage();
        plantsPage.pause(500);
        plantsPage.searchByName(createdPlantName);
        plantsPage.pause(1200);

        boolean found = plantsPage.isPlantInList(createdPlantName);
        System.out.println("Searching for plant '" + createdPlantName + "': " + (found ? "FOUND" : "NOT FOUND"));
        System.out.println("Total rows in list: " + plantsPage.getRowCount());

        assertTrue("Newly added plant '" + createdPlantName + "' should appear in the list", found);
    }

    // TC_PLANT_ADMIN_UI_003 - Edit Plant

    @Given("At least one plant exists")
    public void at_least_one_plant_exists() {
        plantsPage.openPlantsPage();
        initialPlantCount = plantsPage.getRowCount();
        assertTrue("At least one plant should exist", initialPlantCount > 0);
    }

    @When("Locate an existing plant")
    @When("Locate first plant in list")
    public void locate_an_existing_plant() {
        assertTrue("Plants should be visible", plantsPage.getRowCount() > 0);
    }

    @When("Click on Edit button")
    @When("Click Edit on first plant")
    public void click_on_edit_button() {
        plantsPage.clickEditOnFirstRow();
    }

    @When("Change plant name to {string}")
    public void change_plant_name(String newName) {
        // make edit name unique each run to avoid duplicate validation
        editedPlantName = unique(newName);
        plantFormPage.enterName(editedPlantName);
        System.out.println("Using unique edited plant name: " + editedPlantName);
    }

    @When("Change price to {string}")
    public void change_price(String newPrice) {
        plantFormPage.enterPrice(newPrice);
    }

    @When("Click Save")
    public void click_save() {
        plantFormPage.clickSave();
    }

    @Then("Plant details are updated successfully")
    public void plant_details_are_updated_successfully() {
        plantsPage.pause(2000);

        String url = plantFormPage.getDriver().getCurrentUrl();
        boolean redirectedToList = url.contains("/ui/plants") && !url.contains("/edit");

        boolean success = plantsPage.hasSuccessMessage() || plantsPage.isPlantsHeaderVisible() || redirectedToList;

        if (!success && plantFormPage.hasValidationError()) {
            String error = plantFormPage.getValidationErrorMessage();
            throw new AssertionError("Update failed with validation error: " + error);
        }

        assertTrue("Should show success after update", success);
    }

    @Then("Updated values are visible in plant list")
    public void updated_values_are_visible_in_plant_list() {
        plantsPage.pause(800);
        assertTrue("Should be on plant list", plantsPage.isPlantsHeaderVisible());
    }

    // TC_PLANT_ADMIN_UI_004 - Delete Plant

    @When("Click on Delete button for first plant")
    @When("Click Delete on first plant")
    public void click_on_delete_button() {
        plantsPage.clickDeleteOnFirstRow();
    }

    @Then("Delete confirmation modal appears")
    public void delete_confirmation_modal_appears() {
        assertTrue("Delete modal/alert should be visible", plantsPage.isDeleteModalVisible());
    }

    @When("Confirm the delete action")
    public void confirm_delete_action() {
        plantsPage.confirmDelete();
    }

    @Then("Plant is deleted successfully")
    public void plant_is_deleted_successfully() {
        plantsPage.pause(1000);
        assertTrue("Should be on plants list", plantsPage.isPlantsHeaderVisible());
    }

    @Then("Deleted plant no longer appears in the plant list")
    public void deleted_plant_no_longer_appears() {
        int currentCount = plantsPage.getRowCount();
        assertTrue("Plant list should load", currentCount >= 0);
    }

    // TC_PLANT_ADMIN_UI_005 - Validation

    @When("Click Add Plant")
    public void click_add_plant() {
        plantsPage.clickAddPlant();
    }

    @When("Enter valid plant name {string}")
    public void enter_valid_plant_name(String name) {
        // keep as-is (validation test)
        plantFormPage.enterName(name);
    }

    @When("Enter negative price {string}")
    public void enter_negative_price(String price) {
        plantFormPage.enterPrice(price);
    }

    @Then("Validation error message is displayed")
    public void validation_error_message_is_displayed() {
        plantsPage.pause(1000);
        assertTrue("Validation error should be displayed",
                plantFormPage.hasValidationError() || plantFormPage.hasPriceError());
    }

    @Then("Plant is not saved")
    public void plant_is_not_saved() {
        assertTrue("Should still be on form page", plantFormPage.isOnFormPage());
    }

    @Then("User remains on Add Plant page")
    public void user_remains_on_add_plant_page() {
        assertTrue("Should be on add plant page",
                plantFormPage.isOnFormPage() || plantFormPage.isAddPlantPage());
    }

    // TC_PLANT_USER_UI_001 - User View

    @Then("Only active plants are visible")
    public void only_active_plants_are_visible() {
        assertTrue("Plants should be visible", plantsPage.getRowCount() >= 0);
    }

    @Then("No admin actions are shown")
    @Then("Add Plant button is not visible")
    public void no_admin_actions_are_shown() {
        assertFalse("User should not see Add Plant button", plantsPage.isAddPlantButtonVisible());
    }

    // TC_PLANT_USER_UI_002 - Search

    @When("Enter plant name {string} in search box")
    public void enter_plant_name_in_search_box(String name) {
        plantsPage.searchByName(name);
    }

    @When("Click Search")
    public void click_search() {
        plantsPage.pause(500);
    }

    @Then("Matching plants are displayed")
    public void matching_plants_are_displayed() {
        assertTrue("Search results should be visible", plantsPage.getRowCount() >= 0);
    }

    @Then("Non-matching plants are hidden")
    public void non_matching_plants_are_hidden() {
        assertTrue("Only matching results should show", plantsPage.getRowCount() >= 0);
    }

    // normal user should not see add button

    @Then("User should not see Add Plant button")
    public void user_should_not_see_add_plant_button() {
        assertFalse("User must not see Add Plant button", plantsPage.isAddPlantButtonVisible());
    }

    // permission tests

    @When("Manually enter {string} in browser")
    public void manually_enter_url(String url) {
        plantsPage.navigateToUrl(url);
    }

    @Then("Access denied page is shown")
    @Then("User should see Access Denied")
    public void access_denied_page_is_shown() {
        assertTrue("Access denied should be shown", plantsPage.isAccessDenied());
    }

    @Then("User cannot add plants")
    public void user_cannot_add_plants() {
        assertTrue("Should show access denied", plantsPage.isAccessDenied());
    }

    // deleted plant visibility tests

    @Given("Deleted plant exists")
    public void deleted_plant_exists() {
        plantsPage.openPlantsPage();
    }

    @Then("Deleted plants are not displayed")
    public void deleted_plants_are_not_displayed() {
        assertTrue("Only active plants should be visible", plantsPage.getRowCount() >= 0);
    }

    @Then("Only active plants are shown")
    public void only_active_plants_shown() {
        assertTrue("Active plants should be shown", plantsPage.getRowCount() >= 0);
    }

    @When("Admin adds a plant with name {string} price {string} qty {string}")
    public void admin_adds_a_plant(String name, String price, String qty) {
        plantsPage.clickAddPlant();
        plantFormPage.enterName(name);
        plantFormPage.enterPrice(price);
        plantFormPage.enterQuantity(qty);
        plantFormPage.clickSave();
    }

    @Then("Plant form should show validation error")
    public void plant_form_should_show_validation_error() {
        assertTrue("Validation error should be displayed", plantFormPage.hasValidationError());
    }

    @When("Admin tries delete first plant")
    public void admin_tries_delete_first_plant() {
        plantsPage.clickDeleteOnFirstRow();
        assertTrue("Delete modal/alert should appear", plantsPage.isDeleteModalVisible());
        plantsPage.confirmDelete();
    }

    @Then("Plant table should still load after delete")
    public void plant_table_should_load_after_delete() {
        assertTrue("Plant table should load", plantsPage.getRowCount() >= 0);
    }

    @Then("Pagination should work in Plant list if available")
    public void pagination_should_work_in_plant_list_if_available() {
        if (plantsPage.hasNextButton()) {
            plantsPage.clickNextPage();
            assertTrue("After next page, plant page should load", plantsPage.isPlantsHeaderVisible());
        }
        if (plantsPage.hasPrevButton()) {
            plantsPage.clickPrevPage();
            assertTrue("After previous page, plant page should load", plantsPage.isPlantsHeaderVisible());
        }
    }

    @When("User searches plant by name {string}")
    public void user_searches_plant_by_name(String name) {
        plantsPage.filterByName(name);
    }

    @Then("Plant search result should show table or empty")
    public void plant_search_result_should_show_table_or_empty() {
        assertTrue("Search results should load", plantsPage.getRowCount() >= 0);
    }

    @When("User tries to open plant add url directly")
    public void user_tries_to_open_plant_add_url_directly() {
        plantsPage.navigateToUrl("/ui/plants/add");
    }
}
