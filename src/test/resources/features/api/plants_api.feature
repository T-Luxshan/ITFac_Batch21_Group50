@plants_api
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


  # ============================================
  # USER PLANT TESTS
  # ============================================

  @TC_PLANT_USR_API_006 @user @smoke @IFHAM
  Scenario: TC_PLANT_USR_API_006 - USER can retrieve plants by categoryId
    Given api user authenticates as "user"
    And api user gets a valid category id
    When api user sends GET to plants by category
    Then api response status should be 200
    And api response should contain plants list

  @TC_PLANT_USR_API_009 @user @smoke @IFHAM
  Scenario: TC_PLANT_USR_API_009 - Plants summary endpoint works
    Given api user authenticates as "user"
    When api user sends GET to plants summary endpoint
    Then api response status should be 200
    And api response should contain totalPlants
    And api response should contain lowStockPlants
