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
