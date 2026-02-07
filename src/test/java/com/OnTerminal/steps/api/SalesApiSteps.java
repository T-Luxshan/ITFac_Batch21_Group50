package com.OnTerminal.steps.api;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.util.EnvironmentVariables;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import static org.hamcrest.Matchers.*;

public class SalesApiSteps {

    private static final String BASE = System.getProperty("api.base.url", "http://localhost:8080");
    private String token;
    private EnvironmentVariables environmentVariables;
    private int internalSaleId;


    @Given("sales api is authenticated as {string}")
    public void authenticate(String role) {
        String username = environmentVariables.getProperty(role + ".username");
        String password = environmentVariables.getProperty(role + ".password");

        var response = SerenityRest.given()
                .baseUri(BASE)
                .contentType("application/json")
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .post("/api/auth/login")
                .then().extract().response();

        token = response.jsonPath().getString("token");
        if (token == null) token = response.jsonPath().getString("accessToken");
        if (token == null) token = response.jsonPath().getString("jwt");
    }



    @When("sales api user deletes the created sale")
    public void deleteSale() {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .delete("/api/sales/" + internalSaleId);
    }

    @When("sales api user updates the created sale with quantity {int}")
    public void updateSale(int qty) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("{\"quantity\":" + qty + "}")
                .put("/api/sales/" + internalSaleId);
    }

//    @When("sales api user creates sale for plant id {int} with quantity {int}")
//    public void createSale(int plantId, int qty) {
//        SerenityRest.given()
//                .baseUri(BASE)
//                .header("Authorization", "Bearer " + token)
//                .queryParam("quantity", qty)
//                .post("/api/sales/plant/" + plantId);
//    }
@When("sales api user creates sale for plant id {int} with quantity {int}")
public void createSale(int plantId, int qty) {
    var response = SerenityRest.given()
            .baseUri(BASE)
            .header("Authorization", "Bearer " + token)
            .queryParam("quantity", qty)
            .post("/api/sales/plant/" + plantId)
            .then().extract().response();


    if (response.statusCode() == 200 || response.statusCode() == 201) {
        internalSaleId = response.jsonPath().getInt("id");
    }
}

    @When("sales api user requests sales with sort {string}")
    public void getSalesSorted(String sort) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .queryParam("sort", sort)
                .get("/api/sales");
    }

    @When("sales api user requests sales page {int} size {int}")
    public void getSalesPaginated(int page, int size) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .queryParam("page", page)
                .queryParam("size", size)
                .get("/api/sales/page");
    }
    @Given("sales api is not authenticated")
    public void salesApiIsNotAuthenticated() {
        SerenityRest.reset();
    }


    @When("sales api user sends GET {string}")
    public void sendGet(String endpoint) {
        SerenityRest.given()
                .baseUri(BASE)
                .get(endpoint);
    }

    @When("sales api user sends GET {string} without token")
    public void sendGetNoToken(String endpoint) {
        SerenityRest.given()
                .baseUri(BASE)
                .get(endpoint);
    }

    @When("sales api user sends GET for the deleted sale")
    public void getDeletedSale() {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .get("/api/sales/" + internalSaleId);
    }

    @Then("sales api response status should be {int}")
    public void verifyStatus(int code) {
//        System.out.println(code+"ghgggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        SerenityRest.then().statusCode(code);
    }

    @And("sales api response should be sorted by {string} in {string} order")
    public void verifySorting(String field, String order) {
        List<Integer> values = SerenityRest.lastResponse().jsonPath().getList("content." + field);
        if (values != null && values.size() > 1) {
            for (int i = 0; i < values.size() - 1; i++) {
                if (order.equalsIgnoreCase("desc")) {
                    assert values.get(i) >= values.get(i+1);
                } else {
                    assert values.get(i) <= values.get(i+1);
                }
            }
        }
    }

    @And("sales api response should contain pagination metadata with size {int}")
    public void verifyPagination(int size) {
        SerenityRest.then().body("content.size()", equalTo(size))
                .body("$", hasKey("totalElements"))
                .body("$", hasKey("totalPages"));
    }



    @And("sales api response should contain a list of sales")
    public void verifyListExists() {
        SerenityRest.then().body("content", notNullValue());
    }

    @And("sales api response should not contain sensitive fields like {string}")
    public void verifyNoDataLeakage(String field) {
        SerenityRest.then().body("content", everyItem(not(hasKey(field))));
    }
}