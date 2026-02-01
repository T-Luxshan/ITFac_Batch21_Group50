package com.OnTerminal.steps.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;

public class CategoryApiSteps {

    private static final String BASE = System.getProperty("api.base.url", "http://localhost:8080");

    private String token;

    @Given("api user is authenticated as {string}")
    public void api_user_is_authenticated_as(String role) {
        String username = role.equalsIgnoreCase("admin") ? "admin" : "user";
        String password = role.equalsIgnoreCase("admin") ? "admin123" : "user123"; // change if needed

        var response = SerenityRest.given()
                .baseUri(BASE)
                .contentType("application/json")
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .post("/api/auth/login")
                .then()
                .extract()
                .response();

        token = response.jsonPath().getString("token");
        if (token == null)
            token = response.jsonPath().getString("accessToken");
        if (token == null)
            token = response.jsonPath().getString("jwt");

        // If still null, your login response uses a different field name.
        // We'll fix it once you show me the login response body.
    }

    @When("user sends GET {string}")
    public void user_sends_get(String endpoint) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .get(endpoint);
    }

    @Then("response status should be {int}")
    public void response_status_should_be(Integer code) {
        SerenityRest.then().statusCode(code);
    }

}
