@ui @security
Feature: Security UI Tests (Group50)
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

  @TC_AUTH_USR_UI_010 @auth @user @IFHAM
  Scenario: TC_AUTH_USR_UI_010 - Logout shows success message
    Given user is logged in as "user"
    When user clicks logout button
    Then user should see logout success message
    And user should be redirected to login page
    
  # ============================================
  # NAVIGATION TESTS
  # ============================================

  @TC_NAV_USR_UI_009 @navigation @user @IFHAM
  Scenario: TC_NAV_USR_UI_009 - Active navigation highlight
    Given user is logged in as "user"
    When user navigates to categories page
    Then Categories menu item should be highlighted
    When user navigates to plants page
    Then Plants menu item should be highlighted
    When user navigates to sales page
    Then Sales menu item should be highlighted