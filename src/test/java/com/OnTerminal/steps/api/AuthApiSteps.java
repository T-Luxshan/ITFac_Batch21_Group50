package com.OnTerminal.steps.api;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasKey;

public class AuthApiSteps {
    private static final String BASE = System.getProperty("api.base.url", "http://localhost:8080");

    @Given("the admin user exists")
    public void the_admin_user_exists() {
        // Implicitly true for this test env
    }

    @When("I send a POST request to {string} with username {string} and password {string}")
    public void sendPostLogin(String endpoint, String username, String password) {
        SerenityRest.given()
                .baseUri(BASE)
                .contentType("application/json")
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .post(endpoint);
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int code) {
        SerenityRest.then().statusCode(code);
    }

    private String jwtToken;

    @Then("the response should contain a valid JWT token")
    public void verifyJwtToken() {
        SerenityRest.then().body("$", anyOf(hasKey("token"), hasKey("accessToken"), hasKey("jwt")));
        // Extract token for future use
        try {
            jwtToken = SerenityRest.lastResponse().jsonPath().getString("token");
            if (jwtToken == null)
                jwtToken = SerenityRest.lastResponse().jsonPath().getString("accessToken");
            if (jwtToken == null)
                jwtToken = SerenityRest.lastResponse().jsonPath().getString("jwt");
        } catch (Exception e) {
            System.out.println("Could not extract token: " + e.getMessage());
        }
    }

    @Then("the response should not contain a valid JWT token")
    public void verifyNoJwtToken() {
        SerenityRest.then().body("$",
                org.hamcrest.Matchers.not(anyOf(hasKey("token"), hasKey("accessToken"), hasKey("jwt"))));
    }

    @When("I send a POST request to {string}")
    public void sendPostRequest(String endpoint) {
        var req = SerenityRest.given()
                .baseUri(BASE)
                .contentType("application/json");

        if (jwtToken != null && !jwtToken.isEmpty()) {
            req.header("Authorization", "Bearer " + jwtToken);
        }

        req.post(endpoint);
    }
}
