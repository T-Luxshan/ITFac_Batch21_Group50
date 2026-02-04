package com.OnTerminal.steps;

import com.OnTerminal.config.ConfigManager;
import com.OnTerminal.config.Constants;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;

import java.util.HashMap;
import java.util.Map;

/**
 * BaseApiSteps - Foundation for All API Step Definitions
 * ========================================================
 * 
 * This implements the DATA-DRIVEN approach by providing
 * common API functionality that all API step definitions can use.
 */
public abstract class BaseApiSteps {

    protected static final ConfigManager config = ConfigManager.getInstance();
    
    // Test state shared across step definitions
    protected static String authToken;
    protected static Response lastResponse;
    protected static Map<String, Object> testData = new HashMap<>();

    // ==================== Authentication Methods ====================

    /**
     * Authenticate and get token
     */
    protected String authenticate(String role) {
        String username;
        String password;

        if (role.equalsIgnoreCase("admin")) {
            username = Constants.Credentials.ADMIN_USERNAME;
            password = Constants.Credentials.ADMIN_PASSWORD;
        } else {
            // Try user credentials first
            username = Constants.Credentials.USER_USERNAME;
            password = Constants.Credentials.USER_PASSWORD;
        }

        Response response = executeAuth(username, password);

        // User credentials are already correct in Constants
        if (response.statusCode() == Constants.StatusCodes.OK) {
            authToken = extractToken(response);
            System.out.println("[Auth] Successfully authenticated as: " + role);
            return authToken;
        } else {
            System.out.println("[Auth] Authentication failed: " + response.asString());
            throw new RuntimeException("Authentication failed for role: " + role);
        }
    }

    private Response executeAuth(String username, String password) {
        String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        
        return SerenityRest.given()
                .baseUri(config.getApiBaseUrl())
                .contentType("application/json")
                .body(body)
                .post(Constants.UrlPaths.API_AUTH_LOGIN);
    }

    /**
     * Extract token from various response formats
     */
    private String extractToken(Response response) {
        String token = response.jsonPath().getString("token");
        if (token == null) token = response.jsonPath().getString("accessToken");
        if (token == null) token = response.jsonPath().getString("jwt");
        if (token == null) token = response.jsonPath().getString("access_token");
        return token;
    }

    // ==================== Request Methods ====================

    /**
     * Create authenticated request specification
     */
    protected RequestSpecification authenticatedRequest() {
        return SerenityRest.given()
                .baseUri(config.getApiBaseUrl())
                .header("Authorization", "Bearer " + authToken)
                .contentType("application/json");
    }

    /**
     * Create unauthenticated request specification
     */
    protected RequestSpecification unauthenticatedRequest() {
        return SerenityRest.given()
                .baseUri(config.getApiBaseUrl())
                .contentType("application/json");
    }

    /**
     * Create request with custom token (for security tests)
     */
    protected RequestSpecification requestWithToken(String token) {
        return SerenityRest.given()
                .baseUri(config.getApiBaseUrl())
                .header("Authorization", "Bearer " + token)
                .contentType("application/json");
    }

    // ==================== HTTP Methods ====================

    /**
     * Send GET request (Retrieve data)
     */
    protected Response sendGet(String endpoint) {
        logRequest("GET", endpoint);
        lastResponse = authenticatedRequest().get(endpoint);
        logResponse(lastResponse);
        return lastResponse;
    }

    /**
     * Send GET request without authentication
     */
    protected Response sendGetNoAuth(String endpoint) {
        logRequest("GET (No Auth)", endpoint);
        lastResponse = unauthenticatedRequest().get(endpoint);
        logResponse(lastResponse);
        return lastResponse;
    }

    /**
     * Send GET request with custom token
     */
    protected Response sendGetWithToken(String endpoint, String token) {
        logRequest("GET (Custom Token)", endpoint);
        lastResponse = requestWithToken(token).get(endpoint);
        logResponse(lastResponse);
        return lastResponse;
    }

    /**
     * Send POST request (Create)
     */
    protected Response sendPost(String endpoint, Object body) {
        logRequest("POST", endpoint, body);
        lastResponse = authenticatedRequest().body(body).post(endpoint);
        logResponse(lastResponse);
        return lastResponse;
    }

    /**
     * Send POST request without authentication
     */
    protected Response sendPostNoAuth(String endpoint, Object body) {
        logRequest("POST (No Auth)", endpoint, body);
        lastResponse = unauthenticatedRequest().body(body).post(endpoint);
        logResponse(lastResponse);
        return lastResponse;
    }

    /**
     * Send PUT request (Update)
     */
    protected Response sendPut(String endpoint, Object body) {
        logRequest("PUT", endpoint, body);
        lastResponse = authenticatedRequest().body(body).put(endpoint);
        logResponse(lastResponse);
        return lastResponse;
    }

    /**
     * Send DELETE request
     */
    protected Response sendDelete(String endpoint) {
        logRequest("DELETE", endpoint);
        lastResponse = authenticatedRequest().delete(endpoint);
        logResponse(lastResponse);
        return lastResponse;
    }

    // ==================== Response Verification Methods ====================

    /**
     * Verify response status code
     */
    protected void verifyStatusCode(int expectedCode) {
        int actualCode = lastResponse.statusCode();
        if (actualCode != expectedCode) {
            System.out.println("[Verification] Expected: " + expectedCode + ", Actual: " + actualCode);
            System.out.println("[Verification] Response: " + lastResponse.asString());
        }
        assert actualCode == expectedCode : 
            "Expected status code " + expectedCode + " but got " + actualCode;
    }

    /**
     * Verify response contains field
     */
    protected void verifyResponseContainsField(String fieldPath) {
        Object value = lastResponse.jsonPath().get(fieldPath);
        assert value != null : "Response should contain field: " + fieldPath;
    }

    /**
     * Get response field value
     */
    protected <T> T getResponseField(String fieldPath, Class<T> type) {
        return lastResponse.jsonPath().getObject(fieldPath, type);
    }

    /**
     * Verify response is a list
     */
    protected void verifyResponseIsList() {
        assert lastResponse.jsonPath().getList("$") != null : "Response should be a list";
    }

    /**
     * Verify response is not empty list
     */
    protected void verifyResponseIsNotEmptyList() {
        assert !lastResponse.jsonPath().getList("$").isEmpty() : "Response list should not be empty";
    }

    // ==================== Status Code Verification ====================

    /**
     * Verify success status (2xx)
     */
    protected void verifySuccessStatus() {
        int code = lastResponse.statusCode();
        assert code >= 200 && code < 300 : 
            "Expected success status (2xx) but got " + code;
    }

    /**
     * Verify error status (4xx or 5xx)
     */
    protected void verifyErrorStatus() {
        int code = lastResponse.statusCode();
        assert code >= 400 : 
            "Expected error status (4xx/5xx) but got " + code;
    }

    /**
     * Verify client error status (4xx)
     */
    protected void verifyClientErrorStatus() {
        int code = lastResponse.statusCode();
        assert code >= 400 && code < 500 : 
            "Expected client error status (4xx) but got " + code;
    }

    // ==================== Test Data Management ====================

    /**
     * Store test data for later use
     */
    protected void storeTestData(String key, Object value) {
        testData.put(key, value);
        System.out.println("[TestData] Stored: " + key + " = " + value);
    }

    /**
     * Retrieve stored test data
     */
    @SuppressWarnings("unchecked")
    protected <T> T getTestData(String key) {
        return (T) testData.get(key);
    }

    /**
     * Clear all test data
     */
    protected void clearTestData() {
        testData.clear();
        System.out.println("[TestData] Cleared all test data");
    }

    // ==================== Logging Methods ====================

    protected void logRequest(String method, String endpoint) {
        System.out.println("[Request] " + method + " " + endpoint);
    }

    protected void logRequest(String method, String endpoint, Object body) {
        System.out.println("[Request] " + method + " " + endpoint);
        System.out.println("[Request Body] " + body);
    }

    protected void logResponse(Response response) {
        System.out.println("[Response] Status: " + response.statusCode());
        if (response.getBody().asString().length() < 500) {
            System.out.println("[Response Body] " + response.asString());
        } else {
            System.out.println("[Response Body] (truncated) " + response.asString().substring(0, 500) + "...");
        }
    }
}
