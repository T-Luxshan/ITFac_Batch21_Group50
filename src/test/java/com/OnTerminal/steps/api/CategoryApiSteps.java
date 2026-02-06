package com.OnTerminal.steps.api;

import com.OnTerminal.config.Constants;
import com.OnTerminal.steps.BaseApiSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.List;

import static org.junit.Assert.*;

/**
 * CategoryApiSteps - Step definitions for Category API tests
 * Uses BaseApiSteps and Constants for configuration values
 */
public class CategoryApiSteps extends BaseApiSteps {

    private Integer createdCategoryId;

    @Given("api user creates a new category for deletion")
    public void apiUserCreatesANewCategoryForDeletion() {
        String uniqueName = "Del" + (System.currentTimeMillis() % 1000000);
        if (uniqueName.length() > 10) {
            uniqueName = uniqueName.substring(0, 10);
        }

        Response response = sendPost(Constants.UrlPaths.API_CATEGORIES, "{\"name\":\"" + uniqueName + "\"}");

        System.out.println("Create category response: " + response.statusCode() + " - " + response.asString());

        if (response.statusCode() == Constants.StatusCodes.CREATED
                || response.statusCode() == Constants.StatusCodes.OK) {
            createdCategoryId = response.jsonPath().getInt("id");
            System.out.println("TC_CAT_ADM_API_004: Created category ID: " + createdCategoryId);
        } else {
            fail("Failed to create category: " + response.statusCode());
        }
    }

    @When("user sends GET {string}")
    public void user_sends_get(String endpoint) {
        sendGet(endpoint);
    }

    @When("user creates unique category with name {string}")
    public void user_creates_unique_category_with_name(String name) {
        // Validation: Category name must be between 3 and 10 characters
        String prefix = name.length() > 6 ? name.substring(0, 6) : name;
        String uniqueName = prefix + (int) (Math.random() * 900 + 100);

        sendPost(Constants.UrlPaths.API_CATEGORIES, "{\"name\":\"" + uniqueName + "\"}");
    }

    @When("user creates category with name {string}")
    public void user_creates_category_with_name(String name) {
        sendPost(Constants.UrlPaths.API_CATEGORIES, "{\"name\":\"" + name + "\"}");
    }

    @When("user updates category id {int} with name {string}")
    public void user_updates_category_id_with_name(int id, String name) {
        sendPut(Constants.UrlPaths.API_CATEGORIES + "/" + id, "{\"name\":\"" + name + "\"}");
    }

    @When("api user sends DELETE to delete the category")
    public void apiUserSendsDeleteToDeleteTheCategory() {
        assertNotNull("Created category ID should be set", createdCategoryId);
        lastResponse = sendDelete(Constants.UrlPaths.API_CATEGORIES + "/" + createdCategoryId);
        System.out.println("TC_CAT_ADM_API_004: DELETE category response: " + lastResponse.statusCode());
    }

    @Then("response status should be {int}")
    public void response_status_should_be(Integer code) {
        verifyStatusCode(code);
    }

    @Then("category should be removed from system")
    public void categoryShouldBeRemovedFromSystem() {
        assertNotNull("Created category ID should be set", createdCategoryId);
        Response verifyResponse = sendGet(Constants.UrlPaths.API_CATEGORIES + "/" + createdCategoryId);
        System.out.println("TC_CAT_ADM_API_004: Verify deleted category response: " + verifyResponse.statusCode());
        assertEquals("Category should not exist (404)",
                Constants.StatusCodes.NOT_FOUND, verifyResponse.statusCode());
    }

    @Then("api response should contain sub-categories list")
    public void apiResponseShouldContainSubCategoriesList() {
        verifyResponseIsList();

        List<?> categories = lastResponse.jsonPath().getList("$");
        assertNotNull("TC_CAT_ADM_API_003: Response should contain categories list", categories);
        System.out.println("Sub-categories list size: " + categories.size());
    }
}
