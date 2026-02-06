package com.OnTerminal.steps.api;


import java.util.List;

import com.OnTerminal.config.Constants;
import com.OnTerminal.steps.BaseApiSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.Assert.assertNotNull;

/**
 * CategoryApiSteps - Step definitions for Category API tests
 * Uses BaseApiSteps and Constants for configuration values
 */
public class CategoryApiSteps extends BaseApiSteps {

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

    @Then("response status should be {int}")
    public void response_status_should_be(Integer code) {
        verifyStatusCode(code);
    }

    @Then("api response should contain sub-categories list")
    public void apiResponseShouldContainSubCategoriesList() {
        verifyResponseIsList();

        List<?> categories = lastResponse.jsonPath().getList("$");
        assertNotNull("TC_CAT_ADM_API_003: Response should contain categories list", categories);
        System.out.println("Sub-categories list size: " + categories.size());
    }
}
