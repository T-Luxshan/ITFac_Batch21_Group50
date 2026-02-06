@api @plants
Feature: Plants Management API  (Group50)
  API tests for plant-related operations including CRUD and stock management

  # ============================================
  # ADMIN PLANT TESTS
  # ============================================

  @TC_PLANT_ADM_API_002 @admin @negative @validation @IFHAM
  Scenario: TC_PLANT_ADM_API_002 - Admin update Plant with negative quantity rejected
    Given api user authenticates as "admin"
    And api user gets a valid plant id
    When api user sends PUT to update plant with negative quantity
    Then api response status should be 400
    And plant quantity should remain unchanged