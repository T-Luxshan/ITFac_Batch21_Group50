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