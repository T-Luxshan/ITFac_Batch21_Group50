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

  @TC_AUTH_04
  Scenario: Verify password is required validation when random username is entered
    Given the login page is open
    When the user enters username "randomUser"
    And the user leaves password empty
    And the user clicks the login button
    Then the validation message "Password is required" is shown under the password field in red
    And the user remains on the login page

    @TC_AUTH_05
  Scenario: Verify password is required validation when valid admin username is entered
    Given the login page is open
    When the user enters username "admin"
    And the user leaves password empty
    And the user clicks the login button
    Then the validation message "Password is required" is shown under the password field in red
    And the user remains on the login page

  @TC_AUTH_06
  Scenario: Verify password is required validation when valid user username is entered
    Given the login page is open
    When the user enters username "testuser"
    And the user leaves password empty
    And the user clicks the login button
    Then the validation message "Password is required" is shown under the password field in red
    And the user remains on the login page

  @TC_AUTH_07
  Scenario: Verify username and password are required validation when valid user username is entered
    Given the login page is open
    When the user leaves username empty
    And the user leaves password empty
    And the user clicks the login button
    Then the validation message "Username is required" is shown under the username field in red
    And the validation message "Password is required" is shown under the password field in red
    And the user remains on the login page

  @TC_AUTH_08
  Scenario: Verify system denies login when valid admin username is enter but invalid password is entered
    Given the login page is open
    When the user enters username "admin"
    And the user enters password "invalidPassword"
    And the user clicks the login button
    Then the validation message "Invalid username or password." is shown under the password field in red
    And the user remains on the login page

  @TC_AUTH_09
  Scenario: Verify system denies login when valid user username is enter but invalid password is entered
    Given the login page is open
    When the user enters username "testuser"
    And the user enters password "invalidPassword"
    And the user clicks the login button
    Then the validation message "Invalid username or password." is shown under the password field in red
    And the user remains on the login page

  @TC_AUTH_10
  Scenario: Verify system denies invalid username and password are entered
    Given the login page is open
    When the user enters username "invalidUser"
    And the user enters password "invalidPassword"
    And the user clicks the login button
    Then the validation message "Invalid username or password." is shown under the password field in red
    And the user remains on the login page



