@ui @sales
Feature: Sales Management UI
  UI tests for sales-related operations including selling plants and viewing sales records

  # ============================================
  # ADMIN SALES UI TESTS
  # ============================================

  @TC_SALES_ADM_UI_001 @admin @smoke @IFHAM
  Scenario: TC_SALES_ADM_UI_001 - Successful Sell Plant reduces stock and redirects
    Given user is logged in as "admin"
    And user navigates to plants page
    And user notes the stock quantity of first available plant
    When user navigates to sell plant page
    And user selects the noted plant from dropdown
    And user enters quantity "1"
    And user clicks sell button
    Then user should be redirected to sales list page
    And user navigates to plants page
    And the stock quantity should be reduced by 1