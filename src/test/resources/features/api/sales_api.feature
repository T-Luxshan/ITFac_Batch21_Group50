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

  @TC_SEC_USR_API_007 @auth @negative @IFHAM
  Scenario: TC_SEC_USR_API_007 - Secured pagination API requires token
    Given api user has no authentication token
    When api user sends GET to "/api/categories/page?page=0&size=5" without token
    Then api response status should be 401

  @TC_SALES_USR_API_010 @user @IFHAM
  Scenario: TC_SALES_USR_API_010 - User sends sale request
    Given api user authenticates as "user"
    And api user gets a valid plant id with stock
    When api user sends POST to sell plant with quantity 1
    Then api response should be received
