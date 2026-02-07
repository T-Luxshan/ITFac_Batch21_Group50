@inventory_ui
Feature: Inventory Management UI

  @TC_INV_001
  Scenario: Verify Inventory menu item is clickable
    Given user is logged in as "admin"
    When user attempts to click "Inventory" menu item
    Then the Inventory page should be displayed
