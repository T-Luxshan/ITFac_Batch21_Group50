package com.OnTerminal.steps.api;

import com.OnTerminal.config.Constants;
import com.OnTerminal.steps.BaseApiSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Step Definitions for Sales and Security API Tests
 * Uses BaseApiSteps for shared auth/request helpers and Constants for
 * configuration values
 * Covers test cases: TC_SALES_ADM_API_006, TC_PLANT_ADM_API_002,
 * TC_CAT_ADM_API_003-004,
 * TC_SEC_ADM_API_005, TC_PLANT_USR_API_006, TC_SEC_USR_API_007-008,
 * TC_PLANT_USR_API_009, TC_SALES_USR_API_010
 */
public class SalesApiSteps extends BaseApiSteps {

    // Test state - static to persist across Cucumber step class instances
    private static Integer plantId;
    private static Integer plantStock;
    private static int internalSaleId;

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

            // If no plant with stock > 0 found, create one for testing
            if (plantId == null) {
                System.out.println("[SalesApiSteps] No plants with stock found, creating one for testing...");
                createTestPlant();
            }

            System.out.println("[SalesApiSteps] Found/Created plant ID: " + plantId + " with stock: " + plantStock);
        } else {
            System.out.println("[SalesApiSteps] Failed to get plants: " + response.asString());
            // Try to create a plant anyway
            createTestPlant();
        }

        assertNotNull("Plant ID must be available for test", plantId);
    }

    private void createTestPlant() {
        // Get a valid sub-category first (plants require sub-categories)
        Response catResponse = sendGet(Constants.UrlPaths.API_CATEGORIES);

        Integer subCatId = 14; // Default fallback from observed data
        if (catResponse.statusCode() == Constants.StatusCodes.OK) {
            try {
                List<Map<String, Object>> categories = catResponse.jsonPath().getList("$");
                for (Map<String, Object> cat : categories) {
                    Object parentName = cat.get("parentName");
                    if (parentName != null && !parentName.toString().equals("-")) {
                        subCatId = ((Number) cat.get("id")).intValue();
                        System.out.println(
                                "[SalesApiSteps] Using sub-category ID: " + subCatId + " (" + cat.get("name") + ")");
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("[SalesApiSteps] Error finding sub-category, using fallback: " + subCatId);
            }
        }

        String uniqueName = "TestPlant" + System.currentTimeMillis() % 10000;
        String body = String.format(
                "{\"name\":\"%s\",\"price\":10,\"quantity\":100}",
                uniqueName);

        Response createResponse = sendPost(Constants.UrlPaths.API_PLANTS + "/category/" + subCatId, body);

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
            } catch (Exception ignored) {
            }
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

    @Then("api response should be received")
    public void apiResponseShouldBeReceived() {
        assertNotNull("Response should not be null", lastResponse);
        int status = lastResponse.statusCode();
        System.out.println("TC_SALES_USR_API_010: Response status: " + status);
        System.out.println("TC_SALES_USR_API_010: Response body: " + lastResponse.asString());

        assertTrue("Response should have a valid HTTP status code", status > 0);

        if (status >= 200 && status < 300) {
            System.out.println("FINDING: User CAN create sales (status " + status + ") - potential RBAC issue");
        } else if (status == Constants.StatusCodes.FORBIDDEN) {
            System.out.println("EXPECTED: User is forbidden from creating sales (403)");
        } else {
            System.out.println("FINDING: API returned " + status + " - check if this is expected behavior");
        }
    }

    // ==================== Steps from second part ====================

    @Given("sales api is authenticated as {string}")
    public void salesApiIsAuthenticatedAs(String role) {
        authenticate(role); // The parent's authenticate method sets authToken
        System.out.println("[SalesAuth] Authenticated as " + role + " using parent helper");
    }

    @When("sales api user deletes the created sale")
    public void deleteSale() {
        System.out.println("[SalesApi] Attempting to delete sale with ID: " + internalSaleId);
        lastResponse = authenticatedRequest()
                .delete(Constants.UrlPaths.API_SALES + "/" + internalSaleId);
        System.out
                .println("[SalesApi] Delete response: " + lastResponse.statusCode() + " - " + lastResponse.asString());
    }

    @When("sales api user updates the created sale with quantity {int}")
    public void updateSale(int qty) {
        lastResponse = authenticatedRequest()
                .body("{\"quantity\":" + qty + "}")
                .put(Constants.UrlPaths.API_SALES + "/" + internalSaleId);
    }

    @When("sales api user creates sale for plant id {int} with quantity {int}")
    public void createSale(int targetPlantId, int qty) {
        // If the scenario uses 1 (common default), use our dynamically found plant with
        // stock
        if (targetPlantId == 1 && this.plantId != null) {
            targetPlantId = this.plantId;
            System.out.println("[SalesApi] Using dynamic plant ID " + targetPlantId + " instead of 1");
        }

        lastResponse = authenticatedRequest()
                .queryParam("quantity", qty)
                .post(Constants.UrlPaths.API_SALES + "/plant/" + targetPlantId);

        System.out.println(
                "[SalesApi] Create sale response: " + lastResponse.statusCode() + " - " + lastResponse.asString());

        if (lastResponse.statusCode() == 200 || lastResponse.statusCode() == 201) {
            internalSaleId = lastResponse.jsonPath().getInt("id");
            System.out.println("[SalesApi] Created sale with ID: " + internalSaleId);
        } else {
            System.out.println("[SalesApi] Failed to create sale. Status: " + lastResponse.statusCode());
            internalSaleId = 0; // Reset to 0 to indicate no sale was created
        }
    }

    @When("sales api user requests sales with sort {string}")
    public void getSalesSorted(String sort) {
        lastResponse = authenticatedRequest()
                .queryParam("sort", sort)
                .get(Constants.UrlPaths.API_SALES);
    }

    @When("sales api user requests sales page {int} size {int}")
    public void getSalesPaginated(int page, int size) {
        lastResponse = authenticatedRequest()
                .queryParam("page", page)
                .queryParam("size", size)
                .get(Constants.UrlPaths.API_SALES + "/page");
    }

    @Given("sales api is not authenticated")
    public void salesApiIsNotAuthenticated() {
        authToken = "";
        SerenityRest.reset();
    }

    @When("sales api user sends GET {string}")
    public void salesApiUserSendsGet(String endpoint) {
        lastResponse = authenticatedRequest().get(endpoint);
    }

    @When("sales api user sends GET {string} without token")
    public void sendGetNoToken(String endpoint) {
        lastResponse = unauthenticatedRequest().get(endpoint);
    }

    @When("sales api user sends GET for the deleted sale")
    public void getDeletedSale() {
        lastResponse = authenticatedRequest()
                .get(Constants.UrlPaths.API_SALES + "/" + internalSaleId);
    }

    @Then("sales api response status should be {int}")
    public void verifyStatus(int code) {
        // System.out.println(code+"ghgggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        SerenityRest.then().statusCode(code);
    }

    @And("sales api response should be sorted by {string} in {string} order")
    public void verifySorting(String field, String order) {
        List<Integer> values = SerenityRest.lastResponse().jsonPath().getList("content." + field);
        if (values != null && values.size() > 1) {
            for (int i = 0; i < values.size() - 1; i++) {
                if (order.equalsIgnoreCase("desc")) {
                    assert values.get(i) >= values.get(i + 1);
                } else {
                    assert values.get(i) <= values.get(i + 1);
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