package com.OnTerminal.steps.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.util.EnvironmentVariables;

public class CategoryApiSteps {

    private static final String BASE = System.getProperty("api.base.url", "http://localhost:8080");

    private String token;
    private EnvironmentVariables environmentVariables;

    @Given("api user is authenticated as {string}")
    public void api_user_is_authenticated_as(String role) {
        String username, password;
        if (role.equalsIgnoreCase("admin")) {
            username = environmentVariables.getProperty("admin.username");
            password = environmentVariables.getProperty("admin.password");
        } else {
            username = environmentVariables.getProperty("user.username");
            password = environmentVariables.getProperty("user.password");
        }

        var response = SerenityRest.given()
                .baseUri(BASE)
                .contentType("application/json")
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .post("/api/auth/login")
                .then()
                .extract()
                .response();

        if (response.statusCode() != 200) {
            System.out.println("Login failed for " + username + ": " + response.asString());
        }

        token = response.jsonPath().getString("token");
        if (token == null)
            token = response.jsonPath().getString("accessToken");
        if (token == null)
            token = response.jsonPath().getString("jwt");
    }

    @When("user sends GET {string}")
    public void user_sends_get(String endpoint) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .get(endpoint);
    }

    @When("user creates unique category with name {string}")
    public void user_creates_unique_category_with_name(String name) {
        // Validation: Category name must be between 3 and 10 characters
        // We use a shorter prefix if the name is too long, then add a 3-digit random
        // suffix
        String prefix = name.length() > 6 ? name.substring(0, 6) : name;
        String uniqueName = prefix + (int) (Math.random() * 900 + 100);

        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("{\"name\":\"" + uniqueName + "\"}")
                .post("/api/categories");
    }

    @When("user creates category with name {string}")
    public void user_creates_category_with_name(String name) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("{\"name\":\"" + name + "\"}")
                .post("/api/categories");
    }

    @When("user updates category id {int} with name {string}")
    public void user_updates_category_id_with_name(int id, String name) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("{\"name\":\"" + name + "\"}")
                .put("/api/categories/" + id);
    }

    @When("user deletes category id {int}")
    public void user_deletes_category_id(int id) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .delete("/api/categories/" + id);
    }

    @When("user requests category page {int} size {int} sort {string}")
    public void user_requests_category_page_size_sort(int page, int size, String sort) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .get("/api/categories");
    }

    @Then("response status should be {int}")
    public void response_status_should_be(Integer code) {
        SerenityRest.then().statusCode(code);
    }

}
