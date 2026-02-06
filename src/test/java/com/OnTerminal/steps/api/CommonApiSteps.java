package com.OnTerminal.steps.api;

import com.OnTerminal.steps.BaseApiSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class CommonApiSteps extends BaseApiSteps {

    @Given("api user authenticates as {string}")
    public void apiUserAuthenticatesAs(String role) {
        authenticate(role);
    }

    @Given("api user is authenticated as {string}")
    public void apiUserIsAuthenticatedAs(String role) {
        authenticate(role);
    }

    @Then("api response status should be {int}")
    public void apiResponseStatusShouldBe(int code) {
        verifyStatusCode(code);
    }
}
