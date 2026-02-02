Feature: Authentication UI Tests

  @TC_AUTH_01
  Scenario: Verify admin can login using valid credentials
    Given the login page is open
    When the user logs in with username "admin" and password "admin123"
    Then the user is redirected to the dashboard
    And the dashboard loads successfully