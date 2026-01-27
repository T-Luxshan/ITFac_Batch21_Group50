Feature: Category Management (UI)

  Scenario: Verify categories list page loads
    Given user is logged in as admin
    When user opens categories page
    Then categories table should be visible
