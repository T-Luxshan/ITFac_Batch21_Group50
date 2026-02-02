Feature: API Authentication

  @TC_AUTH_11
  Scenario: Verify login with valid credentials for admin
    Given the admin user exists
    When I send a POST request to "/api/auth/login" with username "admin" and password "admin123"
    Then the response status code should be 200
    And the response should contain a valid JWT token
