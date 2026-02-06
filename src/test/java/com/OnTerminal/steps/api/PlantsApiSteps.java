package com.OnTerminal.steps.api;

import com.OnTerminal.config.Constants;
import com.OnTerminal.steps.BaseApiSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PlantsApiSteps extends BaseApiSteps {

    private Integer plantId;
    private Integer originalQuantity;
    private Integer categoryId;

    @Given("api user gets a valid plant id")
    public void apiUserGetsValidPlantId() {
        Response response = sendGet(Constants.UrlPaths.API_PLANTS);

        if (response.statusCode() == Constants.StatusCodes.OK) {
            List<Map<String, Object>> plants = response.jsonPath().getList("$");
            if (plants != null && !plants.isEmpty()) {
                Map<String, Object> firstPlant = plants.get(0);
                plantId = (Integer) firstPlant.get("id");
                Integer qty = (Integer) firstPlant.get("quantity");
                originalQuantity = qty != null ? qty : 0;
                System.out.println("[PlantsApiSteps] Using plant ID: " + plantId
                        + " with quantity: " + originalQuantity);
            }
        }

        if (plantId == null) {
            System.out.println("[PlantsApiSteps] No plants found, creating one for testing...");
            createTestPlant();
        }

        assertNotNull("Plant ID must be available for test", plantId);
    }

    @Given("api user gets a valid category id")
    public void apiUserGetsAValidCategoryId() {
        Response response = sendGet(Constants.UrlPaths.API_CATEGORIES);

        if (response.statusCode() == Constants.StatusCodes.OK) {
            List<Map<String, Object>> categories = response.jsonPath().getList("$");
            if (categories != null && !categories.isEmpty()) {
                Map<String, Object> firstCategory = categories.get(0);
                categoryId = (Integer) firstCategory.get("id");
                System.out.println("[PlantsApiSteps] Using category ID: " + categoryId);
            }
        }

        if (categoryId == null) {
            System.out.println("[PlantsApiSteps] No category found, using default ID: 1");
            categoryId = 1;
        }
    }

    @When("api user sends PUT to update plant with negative quantity")
    public void apiUserUpdatePlantWithNegativeQuantity() {
        assertNotNull("Plant ID should be set", plantId);

        Response getResponse = sendGet(Constants.UrlPaths.API_PLANTS + "/" + plantId);
        System.out.println("TC_PLANT_ADM_API_002: Get plant response: " + getResponse.asString());

        if (getResponse.statusCode() == Constants.StatusCodes.OK) {
            String name = getResponse.jsonPath().getString("name");
            Double price = getResponse.jsonPath().getDouble("price");

            Integer catId = null;
            try {
                catId = getResponse.jsonPath().getInt("category.id");
            } catch (Exception e1) {
                try {
                    catId = getResponse.jsonPath().getInt("categoryId");
                } catch (Exception e2) {
                    System.out.println("Could not extract category ID, using default 1");
                }
            }
            if (catId == null) {
                catId = 1;
            }

            String payload = String.format(
                    "{\"name\":\"%s\",\"price\":%s,\"quantity\":-1,\"categoryId\":%d}",
                    name, price, catId
            );

            System.out.println("TC_PLANT_ADM_API_002: Sending payload: " + payload);
            lastResponse = sendPut(Constants.UrlPaths.API_PLANTS + "/" + plantId, payload);
            System.out.println("TC_PLANT_ADM_API_002: Update plant with negative quantity response: "
                    + lastResponse.statusCode() + " - " + lastResponse.asString());
        } else {
            fail("Failed to get plant data: " + getResponse.statusCode());
        }
    }

    @When("api user sends GET to plants summary endpoint")
    public void apiUserSendsGetToPlantsSummaryEndpoint() {
        lastResponse = sendGet(Constants.UrlPaths.API_PLANTS + "/summary");
        System.out.println("TC_PLANT_USR_API_009: Get plants summary response: "
                + lastResponse.statusCode() + " - " + lastResponse.asString());
    }

    @When("api user sends GET to plants by category")
    public void apiUserSendsGetToPlantsByCategory() {
        assertNotNull("Category ID should be set", categoryId);
        lastResponse = sendGet(Constants.UrlPaths.API_PLANTS + "/category/" + categoryId);
        System.out.println("TC_PLANT_USR_API_006: Get plants by category response: "
                + lastResponse.statusCode() + " - " + lastResponse.asString());
    }

    @Then("plant quantity should remain unchanged")
    public void plantQuantityShouldRemainUnchanged() {
        assertNotNull("Plant ID should be set", plantId);
        assertNotNull("Original quantity should be set", originalQuantity);

        Response verifyResponse = sendGet(Constants.UrlPaths.API_PLANTS + "/" + plantId);
        int currentQuantity = verifyResponse.jsonPath().getInt("quantity");

        System.out.println("TC_PLANT_ADM_API_002: Original quantity: " + originalQuantity
                + ", Current quantity: " + currentQuantity);

        assertEquals("Plant quantity should remain unchanged",
                originalQuantity.intValue(), currentQuantity);
    }

    @Then("api response should contain totalPlants")
    public void apiResponseShouldContainTotalPlants() {
        Object totalPlants = lastResponse.jsonPath().get("totalPlants");
        assertNotNull("TC_PLANT_USR_API_009: Response should contain totalPlants ", totalPlants);
        System.out.println("Total plants: " + totalPlants);
    }

    @Then("api response should contain lowStockPlants")
    public void apiResponseShouldContainLowStockPlants() {
        Object lowStockPlants = lastResponse.jsonPath().get("lowStockPlants");
        assertNotNull("TC_PLANT_USR_API_009: Response should contain lowStockPlants ", lowStockPlants);
        System.out.println("Low stock plants: " + lowStockPlants);
    }

    @Then("api response should contain plants list")
    public void apiResponseShouldContainPlantsList() {
        verifyResponseIsList();
        List<?> plants = lastResponse.jsonPath().getList("$");
        assertNotNull("TC_PLANT_USR_API_006: Response should contain plants list", plants);
        System.out.println("Plants list size: " + plants.size());
    }

    private void createTestPlant() {
        Response catResponse = sendGet(Constants.UrlPaths.API_CATEGORIES);

        Integer catId = 1;
        if (catResponse.statusCode() == Constants.StatusCodes.OK) {
            try {
                catId = catResponse.jsonPath().getInt("[0].id");
            } catch (Exception e) {
                System.out.println("[PlantsApiSteps] Using default category ID: 1");
            }
        }

        String uniqueName = "TestPlant" + System.currentTimeMillis() % 10000;
        String body = String.format(
                "{\"name\":\"%s\",\"price\":10.00,\"quantity\":100,\"categoryId\":%d}",
                uniqueName, catId);

        Response createResponse = sendPost(Constants.UrlPaths.API_PLANTS, body);

        System.out.println("[PlantsApiSteps] Create plant response: " + createResponse.statusCode()
                + " - " + createResponse.asString());

        if (createResponse.statusCode() == Constants.StatusCodes.CREATED
                || createResponse.statusCode() == Constants.StatusCodes.OK) {
            plantId = createResponse.jsonPath().getInt("id");
            originalQuantity = 100;
            System.out.println("[PlantsApiSteps] Created test plant ID: " + plantId);
        }
    }
}
