package com.OnTerminal.steps.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import java.util.List;
import java.util.Map;

public class PlantApiSteps {

  private static final String BASE = System.getProperty("api.base.url", "http://localhost:8080");
  private static String jwtToken;
  private static int lastCreatedPlantId;

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
    if (jwtToken == null) {
      jwtToken = SerenityRest.lastResponse().jsonPath().getString("jwt");
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
    int validSubCategoryId = -1;

    try {
      // 1. Fetch all categories and try to find an existing sub-category
      var response = SerenityRest.given()
          .baseUri(BASE)
          .header("Authorization", "Bearer " + jwtToken)
          .get("/api/categories");

      System.out.println("Categories List: " + response.getBody().asString());

      List<Map<String, Object>> categories = response.jsonPath().getList("$");
      for (Map<String, Object> cat : categories) {
        // Check if 'parentName' exists and is not "-" (indicating a sub-category)
        Object parentName = cat.get("parentName");
        if (parentName != null && !parentName.toString().equals("-")) {
          validSubCategoryId = ((Number) cat.get("id")).intValue();
          System.out.println("Found existing sub-category: " + validSubCategoryId + " (" + cat.get("name") + ")");
          break;
        }
      }
    } catch (Exception e) {
      System.out.println("Error fetching/parsing categories: " + e.getMessage());
    }

    // 2. If no existing sub-category found, try to create one
    if (validSubCategoryId == -1) {
      System.out.println("No existing sub-category found. Attempting to create one under category " + categoryId);
      try {
        String subCatName = "Sub_" + System.currentTimeMillis();
        String subCatBody = "{\"name\":\"" + subCatName + "\", \"parent\":{\"id\":" + categoryId + "}}";

        var createResponse = SerenityRest.given()
            .baseUri(BASE)
            .header("Authorization", "Bearer " + jwtToken)
            .contentType("application/json")
            .body(subCatBody)
            .post("/api/categories");

        if (createResponse.statusCode() == 201) {
          validSubCategoryId = createResponse.jsonPath().getInt("id");
          System.out.println("Created new sub-category: " + validSubCategoryId);
        } else {
          System.out.println(
              "Failed to create sub-category (Status " + createResponse.statusCode() + "). Fallback to " + categoryId);
          validSubCategoryId = categoryId;
        }
      } catch (Exception e) {
        System.out.println("Exception creating sub-category: " + e.getMessage());
        validSubCategoryId = categoryId;
      }
    }

    String uniqueName = name;
    if (name != null && !name.isEmpty()) {
      uniqueName = (name.length() > 8 ? name.substring(0, 8) : name) + "_" + (int) (Math.random() * 10000);
    }
    String body = """
        {
          "name": "%s",
          "price": %d,
          "quantity": %d
        }
        """.formatted(uniqueName, price, quantity);

    SerenityRest.given()
        .baseUri(BASE)
        .header("Authorization", "Bearer " + jwtToken)
        .contentType("application/json")
        .body(body)
        .post("/api/plants/category/" + validSubCategoryId); // Use valid sub-category or fallback

    if (SerenityRest.lastResponse().statusCode() == 201) {
      lastCreatedPlantId = SerenityRest.lastResponse().jsonPath().getInt("id");
      System.out.println("Created plant with ID: " + lastCreatedPlantId);
    } else {
      System.out.println("Failed to create plant. Status: " + SerenityRest.lastResponse().statusCode());
      System.out.println("Response: " + SerenityRest.lastResponse().getBody().asString());
    }
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
  public void i_update_plant_with_json(Integer id, String name, int categoryId, String categoryName, int parentId,
      String parentName, int price, int quantity) {
    updatePlant(id, name, categoryId, categoryName, parentId, parentName, price, quantity);
  }

  @When("I update the created plant with name {string}, categoryId {int}, categoryName {string}, parentId {int}, parentName {string}, price {int}, quantity {int}")
  public void i_update_created_plant_with_json(String name, int categoryId, String categoryName, int parentId,
      String parentName, int price, int quantity) {
    updatePlant(lastCreatedPlantId, name, categoryId, categoryName, parentId, parentName, price, quantity);
  }

  private void updatePlant(Integer id, String name, int categoryId, String categoryName, int parentId,
      String parentName, int price, int quantity) {
    String uniqueName = name;
    if (name != null && !name.isEmpty()) {
      uniqueName = (name.length() > 8 ? name.substring(0, 8) : name) + "_" + (int) (Math.random() * 10000);
    }
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
            }
          }
        }
        """.formatted(uniqueName, price, quantity, categoryId, categoryName, parentId, parentName);

    System.out.println("Updating plant ID: " + id + " with body: " + body);
    var response = SerenityRest.given()
        .baseUri(BASE)
        .header("Authorization", "Bearer " + jwtToken)
        .contentType("application/json")
        .body(body)
        .put("/api/plants/" + id);

    if (response.statusCode() != 200) {
      System.out.println("Update failed. Status: " + response.statusCode());
      System.out.println("Response: " + response.getBody().asString());
    }
  }

  // delete plant
  @When("I delete plant with id {int}")
  public void i_delete_plant_with_id(Integer id) {
    SerenityRest.given()
        .baseUri(BASE)
        .header("Authorization", "Bearer " + jwtToken)
        .delete("/api/plants/" + id);
  }

  @When("I delete the created plant")
  public void i_delete_created_plant() {
    if (lastCreatedPlantId == 0) {
      throw new RuntimeException("No plant was created in previous steps to delete.");
    }
    i_delete_plant_with_id(lastCreatedPlantId);
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
