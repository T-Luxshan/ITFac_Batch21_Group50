@ui @security
Feature: Security UI Tests
  UI tests for authentication, authorization, navigation guards, and session management

  # ============================================
  # AUTHORIZATION TESTS (Access Control)
  # ============================================

  @TC_SEC_ADM_UI_005 @authorization @negative @IFHAM
  Scenario: TC_SEC_ADM_UI_005 - Unauthorized access redirects to Login
    Given user is not logged in
    When user navigates directly to categories page
    Then user should be redirected to login page

  @TC_SEC_USR_UI_006 @authorization @user @negative @IFHAM
  Scenario: TC_SEC_USR_UI_006 - USER cannot see Sell Plant button
    Given user is logged in as "user"
    When user navigates to sales page
    Then Sell Plant button should not be visible

  @TC_SEC_USR_UI_007 @authorization @user @negative @IFHAM
  Scenario: TC_SEC_USR_UI_007 - User role blocked from Admin-only Plant Edit page
    Given user is logged in as "user"
    When user navigates directly to plant edit page with id "1"
    Then user should see access denied page
