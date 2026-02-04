package com.OnTerminal.steps.api;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import static org.hamcrest.Matchers.equalTo;

public class HealthCheckApiSteps {
    private static final String BASE = System.getProperty("api.base.url", "http://localhost:8080");

    @When("I send a GET request to {string}")
    public void sendGetRequest(String endpoint) {
        var req = SerenityRest.given()
                .baseUri(BASE)
                .contentType("application/json");

        if (AuthApiSteps.jwtToken != null && !AuthApiSteps.jwtToken.isEmpty()) {
            req.header("Authorization", "Bearer " + AuthApiSteps.jwtToken);
        }

        req.get(endpoint);
    }

    @Then("the response should contain {string} with value {string}")
    public void verifyResponseValue(String key, String value) {
        SerenityRest.then().body(key, equalTo(value));
    }
}
