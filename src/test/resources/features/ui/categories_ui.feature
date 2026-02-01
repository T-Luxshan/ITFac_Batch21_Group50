Feature: Category Management UI (Group50)

  Scenario: TC_CAT_001 Verify categories list page loads with pagination
    Given user is logged in as "admin"
    When user opens categories page
    Then categories table should be visible
    And pagination controls should be visible
  
  Scenario: TC_CAT_002 Verify searching filters categories by name
    Given user is logged in as "admin"
    When user opens categories page
    And user searches category by keyword "Rose"
    Then results should contain category name "Rose"