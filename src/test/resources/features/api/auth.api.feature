@API
Feature: API Authentication

  @TC_AUTH_11
  Scenario: Verify login with valid credentials for admin
    Given the admin user exists
    When I send a POST request to "/api/auth/login" with username "admin" and password "admin123"
    Then the response status code should be 200
    And the response should contain a valid JWT token

  @TC_AUTH_12
  Scenario: Verify login with valid credentials for user
    Given the admin user exists
    When I send a POST request to "/api/auth/login" with username "testuser" and password "test123"
    Then the response status code should be 200
    And the response should contain a valid JWT token

  @TC_AUTH_13
  Scenario: Verify login with invalid credentials
    When I send a POST request to "/api/auth/login" with username "invalid" and password "invalid"
    Then the response status code should be 401
    And the response should not contain a valid JWT token

    # TODO : verify user able to logout.
