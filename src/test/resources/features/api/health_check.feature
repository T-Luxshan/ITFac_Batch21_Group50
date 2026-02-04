@API
Feature: API Health Check

  @TC_HEALTH_01
  Scenario: Verify API health status
    Given the admin user exists
    When I send a POST request to "/api/auth/login" with username "admin" and password "admin123"
    Then the response status code should be 200
    And the response should contain a valid JWT token
    When I send a GET request to "/api/health"
    Then the response status code should be 200
    And the response should contain "status" with value "UP"
