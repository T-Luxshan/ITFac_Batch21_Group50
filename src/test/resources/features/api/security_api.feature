@api @security
Feature: Security API Tests  (Group50)
  API tests for authentication, authorization, and security validations

  # ============================================
  # AUTHENTICATION TESTS
  # ============================================

  @TC_SEC_ADM_API_005 @auth @negative @IFHAM
  Scenario: TC_SEC_ADM_API_005 - GET categories with malformed token returns 401
    Given api user has malformed token "abc.def.xyz"
    When api user sends GET to "/api/categories" with malformed token
    Then api response status should be 401
