package com.OnTerminal.steps.api;

import com.OnTerminal.steps.BaseApiSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SecurityApiSteps extends BaseApiSteps {

    private String malformedToken;

    @Given("api user has malformed token {string}")
    public void apiUserHasMalformedToken(String token) {
        malformedToken = token;
        System.out.println("[SecurityApiSteps] TC_SEC_ADM_API_005: Using malformed token: " + token);
    }

    @When("api user sends GET to {string} with malformed token")
    public void apiUserSendsGetWithMalformedToken(String endpoint) {
        lastResponse = sendGetWithToken(endpoint, malformedToken);
        System.out.println("TC_SEC_ADM_API_005: GET with malformed token response: " + lastResponse.statusCode());
    }

    @Given("api user has no authentication token")
    public void apiUserHasNoAuthenticationToken() {
        authToken = null;
    }

    @When("api user sends GET to {string} without token")
    public void apiUserSendsGetWithoutToken(String endpoint) {
        lastResponse = sendGetNoAuth(endpoint);
        System.out.println("GET without token response: " + lastResponse.statusCode());
    }

    @Then("all returned categories should have null parent")
    public void allReturnedCategoriesShouldHaveNullParent() {
        verifyResponseIsList();
        List<Map<String, Object>> categories = lastResponse.jsonPath().getList("$");
        assertNotNull("TC_SEC_USR_API_008: Response should contain categories list", categories);

        for (Map<String, Object> category : categories) {
            Object parent = category.get("parentCategory");
            Object parentId = category.get("parentId");

            boolean hasNoParent = (parent == null) || (parentId == null)
                    || ("null".equals(parent.toString()));

            assertTrue("TC_SEC_USR_API_008: Category '" + category.get("name")
                    + "' should have null parent", hasNoParent);
        }

        System.out.println("TC_SEC_USR_API_008: All " + categories.size() + " categories have null parent");
    }
}
