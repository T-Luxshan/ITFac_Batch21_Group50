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

/**
 * Step Definitions for Sales and Security API Tests
 * Uses BaseApiSteps for shared auth/request helpers and Constants for configuration values
 * Covers test cases: TC_SALES_ADM_API_006, TC_PLANT_ADM_API_002, TC_CAT_ADM_API_003-004,
 *                    TC_SEC_ADM_API_005, TC_PLANT_USR_API_006, TC_SEC_USR_API_007-008,
 *                    TC_PLANT_USR_API_009, TC_SALES_USR_API_010
 */
public class SalesApiSteps extends BaseApiSteps {

    // Test state
    private Integer plantId;
    private Integer plantStock;

    // ==================== Data Setup Steps ====================

    @Given("api user gets a valid plant id with stock")
    public void apiUserGetsValidPlantIdWithStock() {
        Response response = sendGet(Constants.UrlPaths.API_PLANTS);

        if (response.statusCode() == Constants.StatusCodes.OK) {
            List<Map<String, Object>> plants = response.jsonPath().getList("$");
            
            // Find first plant with stock > 0
            for (Map<String, Object> plant : plants) {
                Integer qty = (Integer) plant.get("quantity");
                if (qty != null && qty > 0) {
                    plantId = (Integer) plant.get("id");
                    plantStock = qty;
                    break;
                }
            }
            
            // If no plant with stock, use first plant or create one
            if (plantId == null && !plants.isEmpty()) {
                Map<String, Object> firstPlant = plants.get(0);
                plantId = (Integer) firstPlant.get("id");
                plantStock = firstPlant.get("quantity") != null ? (Integer) firstPlant.get("quantity") : 0;
                System.out.println("[SalesApiSteps] Using first plant ID: " + plantId + " (stock: " + plantStock + ")");
            }
            
            // If still no plant, create one for testing
            if (plantId == null) {
                System.out.println("[SalesApiSteps] No plants found, creating one for testing...");
                createTestPlant();
            }
            
            System.out.println("[SalesApiSteps] Found plant ID: " + plantId + " with stock: " + plantStock);
        } else {
            System.out.println("[SalesApiSteps] Failed to get plants: " + response.asString());
            // Try to create a plant anyway
            createTestPlant();
        }
        
        assertNotNull("Plant ID must be available for test", plantId);
    }
    
    private void createTestPlant() {
        // Get a valid category first
        Response catResponse = sendGet(Constants.UrlPaths.API_CATEGORIES);

        Integer catId = 1;
        if (catResponse.statusCode() == Constants.StatusCodes.OK) {
            try {
                catId = catResponse.jsonPath().getInt("[0].id");
            } catch (Exception e) {
                System.out.println("[SalesApiSteps] Using default category ID: 1");
            }
        }

        String uniqueName = "TestPlant" + System.currentTimeMillis() % 10000;
        String body = String.format(
                "{\"name\":\"%s\",\"price\":10.00,\"quantity\":100,\"categoryId\":%d}",
                uniqueName, catId);

        Response createResponse = sendPost(Constants.UrlPaths.API_PLANTS, body);

        System.out.println("[SalesApiSteps] Create plant response: " + createResponse.statusCode() + " - "
                + createResponse.asString());

        if (createResponse.statusCode() == Constants.StatusCodes.CREATED
                || createResponse.statusCode() == Constants.StatusCodes.OK) {
            plantId = createResponse.jsonPath().getInt("id");
            plantStock = 100;
            System.out.println("[SalesApiSteps] Created test plant ID: " + plantId);
        }
    }

    // =============== When Steps - Sales ====================

    @When("api user sends POST to sell plant with quantity {int}")
    public void apiUserSellsPlantWithQuantity(int quantity) {
        assertNotNull("Plant ID should be set", plantId);

        lastResponse = authenticatedRequest()
                .queryParam("quantity", quantity)
                .post(Constants.UrlPaths.API_SALES + "/plant/" + plantId);

        System.out.println("Sell plant response (POST /api/sales/plant/{plantId}): "
                + lastResponse.statusCode() + " - " + lastResponse.asString());
    }

    // ==================== Then Steps ====================

    @Then("api response status should be error")
    public void apiResponseStatusShouldBeError() {
        verifyErrorStatus();
    }

    @Then("api response should contain error message")
    public void apiResponseShouldContainErrorMessage() {
        assertNotNull("Response should not be null", lastResponse);

        String[] expectedMessages = {
                "Invalid quantity",
                Constants.ValidationMessages.QUANTITY_POSITIVE
        };
        String body = lastResponse.asString();
        String lowerBody = body == null ? "" : body.toLowerCase();

        boolean found = false;
        for (String msg : expectedMessages) {
            if (lowerBody.contains(msg.toLowerCase())) {
                found = true;
                break;
            }
        }

        if (!found) {
            try {
                String msg = lastResponse.jsonPath().getString("message");
                String err = lastResponse.jsonPath().getString("error");
                String det = lastResponse.jsonPath().getString("details");
                for (String em : expectedMessages) {
                    String emLow = em.toLowerCase();
                    if ((msg != null && msg.toLowerCase().contains(emLow)) ||
                        (err != null && err.toLowerCase().contains(emLow)) ||
                        (det != null && det.toLowerCase().contains(emLow))) {
                        found = true;
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }

        assertTrue("Response should contain an expected error message", found);
    }

    @Then("api response should contain sales list")
    public void apiResponseShouldContainSalesList() {
        verifyResponseIsList();
        List<?> sales = lastResponse.jsonPath().getList("$");
        assertNotNull("Response should contain sales list", sales);
        System.out.println("Sales list size: " + sales.size());
    }
}
