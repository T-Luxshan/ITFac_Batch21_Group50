@api @sales
Feature: Sales Management API  (Group50)
  API tests for sales-related operations including selling plants and sales records

  # ============================================
  # ADMIN SALES API TESTS
  # ============================================

  @TC_SALES_ADM_API_006 @admin @negative @validation @IFHAM
  Scenario: TC_SALES_ADM_API_006 - Admin cannot sell plant with quantity = 0
    Given api user authenticates as "admin"
    And api user gets a valid plant id with stock
    When api user sends POST to sell plant with quantity 0
    Then api response status should be error
    And api response should contain error message
