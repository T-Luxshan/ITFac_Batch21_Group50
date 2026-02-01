Feature: Category Management UI (Group50)

  Scenario: TC_CAT_001 Verify categories list page loads with pagination
    Given user is logged in as "admin"
    When user opens categories page
    Then categories table should be visible
    And pagination controls should be visible