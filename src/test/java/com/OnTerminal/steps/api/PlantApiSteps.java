package com.OnTerminal.steps.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;

public class PlantApiSteps {

    private static final String BASE = System.getProperty("api.base.url", "http://localhost:9090");
    private static String jwtToken;

    // login
    @Given("I login as {string} with password {string}")
    public void i_login_as_with_password(String username, String password) {
        SerenityRest.given()
                .baseUri(BASE)
                .contentType("application/json")
                .body("""
                        {
                          "username": "%s",
                          "password": "%s"
                        }
                        """.formatted(username, password))
                .post("/api/auth/login");

        jwtToken = SerenityRest.lastResponse().jsonPath().getString("token");
        if (jwtToken == null) {
            jwtToken = SerenityRest.lastResponse().jsonPath().getString("accessToken");
        }
    }

    // get all plants
    @When("I send GET request to {string}")
    public void i_send_get_request_to(String endpoint) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + jwtToken)
                .get(endpoint);
    }

    // create plant
    @When("I create a plant with name {string}, categoryId {int}, price {int}, quantity {int}")
    public void i_create_a_plant_with_json(String name, int categoryId, int price, int quantity) {
        String body = """
            {
              "name": "%s",
              "price": %d,
              "quantity": %d
            }
            """.formatted(name, price, quantity); // category now in URL

        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(body)
                .post("/api/plants/category/" + categoryId); // categoryId in URL
    }

    // get plant by id
    @When("I get plant with id {int}")
    public void i_get_plant_with_id(Integer id) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + jwtToken)
                .get("/api/plants/" + id);
    }


    @When("I update plant with id {int}, name {string}, categoryId {int}, categoryName {string}, parentId {int}, parentName {string}, price {int}, quantity {int}")
    public void i_update_plant_with_json(Integer id, String name, int categoryId, String categoryName, int parentId, String parentName, int price, int quantity) {
        String body = """
    {
      "name": "%s",
      "price": %d,
      "quantity": %d,
      "category": {
        "id": %d,
        "name": "%s",
        "parent": {
          "id": %d,
          "name": "%s"
        },
        "subCategories": []
      }
    }
    """.formatted(name, price, quantity, categoryId, categoryName, parentId, parentName);

        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .body(body)
                .put("/api/plants/" + id);
    }




    // delete plant
    @When("I delete plant with id {int}")
    public void i_delete_plant_with_id(Integer id) {
        SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + jwtToken)
                .delete("/api/plants/" + id);
    }

    // without token
    @When("I send GET request without token to {string}")
    public void i_send_get_request_without_token_to(String endpoint) {
        SerenityRest.given()
                .baseUri(BASE)
                .get(endpoint);
    }

    @When("I create plant without token")
    public void i_create_plant_without_token() {
        String body = """
            {
              "name": "UnauthorizedPlant",
              "price": 100,
              "quantity": 10
            }
            """;

        SerenityRest.given()
                .baseUri(BASE)
                .contentType("application/json")
                .body(body)
                .post("/api/plants/category/1"); // default categoryId
    }
}
