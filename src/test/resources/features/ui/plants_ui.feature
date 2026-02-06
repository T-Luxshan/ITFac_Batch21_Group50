@ui @plants
Feature: Plants Management UI (Group50)
  UI tests for plant-related operations including add, edit, and list views

  # ============================================
  # ADMIN PLANT UI TESTS
  # ============================================

  @TC_CAT_ADM_UI_003 @admin @IFHAM
  Scenario: TC_CAT_ADM_UI_003 - Cancel button returns to Plant List
    Given user is logged in as "admin"
    And user navigates to plants page
    When user clicks Add Plant button
    And user enters plant name "TestPlant"
    And user enters plant price "10.00"
    And user clicks cancel button
    Then user should be redirected to plants list page
    And new plant should not be created