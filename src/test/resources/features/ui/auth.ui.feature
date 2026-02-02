Feature: Authentication UI Tests

  @TC_AUTH_01
  Scenario: Verify admin can login using valid credentials
    Given the login page is open
    When the user logs in with username "admin" and password "admin123"
    Then the user is redirected to the dashboard
    And the dashboard loads successfully

  @TC_AUTH_02
  Scenario: Verify user can login using valid credentials
    Given the login page is open
    When the user logs in with username "testuser" and password "test123"
    Then the user is redirected to the dashboard
    And the dashboard loads successfully

  @TC_AUTH_03
  Scenario: Verify username is required
    Given the login page is open
    When the user leaves username empty
    And the user enters password "anyPassword"
    And the user clicks the login button
    Then the validation message "Username is required" is shown under the username field in red
    And the user remains on the login page