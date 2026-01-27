package com.OnTerminal.steps.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;

public class CategoryApiSteps {

    private String token;

    @Given("admin is authenticated via API")
    public void admin_is_authenticated_via_api() {
        var response = SerenityRest.given()
                .baseUri("http://localhost:8080")
                .contentType("application/json")
                .body("{\"username\":\"admin\",\"password\":\"admin123\"}")
                .post("/api/auth/login")
                .then()
                .extract()
                .response();

        // token field might be "token" or "accessToken" depending on implementation
        token = response.jsonPath().getString("token");
        if (token == null) {
            token = response.jsonPath().getString("accessToken");
        }
    }

    @When("admin requests categories list")
    public void admin_requests_categories_list() {
        SerenityRest.given()
                .baseUri("http://localhost:8080")
                .header("Authorization", "Bearer " + token)
                .get("/api/categories");
    }

    @Then("API should return categories successfully")
    public void api_should_return_categories_successfully() {
        SerenityRest.then().statusCode(200);
    }
}
