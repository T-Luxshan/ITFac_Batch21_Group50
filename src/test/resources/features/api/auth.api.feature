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

  @TC_AUTH_14
  Scenario: Verify login with missing username
    When I send a POST request to "/api/auth/login" with username "" and password "somePassword"
    Then the response status code should be 401
    And the response should not contain a valid JWT token

  @TC_AUTH_15
  Scenario: Verify login with invalid username and empty password
    When I send a POST request to "/api/auth/login" with username "invalidUser" and password ""
    Then the response status code should be 401
    And the response should not contain a valid JWT token

  @TC_AUTH_16
  Scenario: Verify login with valid admin username and missing password
    Given the admin user exists
    When I send a POST request to "/api/auth/login" with username "admin" and password ""
    Then the response status code should be 401
    And the response should not contain a valid JWT token

  @TC_AUTH_17
  Scenario: Verify login with valid testuser username and missing password
    Given the admin user exists
    When I send a POST request to "/api/auth/login" with username "testuser" and password ""
    Then the response status code should be 401
    And the response should not contain a valid JWT token

  @TC_AUTH_18
  Scenario: Verify login with valid admin username and invalid password
    Given the admin user exists
    When I send a POST request to "/api/auth/login" with username "admin" and password "wrongpassword"
    Then the response status code should be 401
    And the response should not contain a valid JWT token

  @TC_AUTH_19
  Scenario: Verify login with valid testuser username and invalid password
    Given the admin user exists
    When I send a POST request to "/api/auth/login" with username "testuser" and password "wrongpassword"
    Then the response status code should be 401
    And the response should not contain a valid JWT token

  @TC_AUTH_20
  Scenario: Verify login with missing username and missing password
    When I send a POST request to "/api/auth/login" with username "" and password ""
    Then the response status code should be 401
    And the response should not contain a valid JWT token


