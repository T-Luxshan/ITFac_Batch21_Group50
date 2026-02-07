@rajitha
Feature: Sales Management UI

  @UI-SALE-001
  Scenario: Verify sorting functionality by "Total Price" column
    Given user is logged in as "admin"
    When user opens sales page
    And user sorts sales by "Total Price"
    Then sales should be sorted by Total Price

  @UI-SALE-002
  Scenario: Verify pagination functionality on Sales list
    Given user is logged in as "admin"
    And user ensures there are at least 11 sales records
    When user opens sales page
    And user navigates to the next page
    Then the next set of records should be displayed

  @UI-SALE-003
  Scenario: Verify Cancel button behavior
    Given user is logged in as "admin"
    When user opens sales page
    And user clicks Add Sale
    And user clicks Cancel on sales page
    Then user should be on sales list page
    And no new sale should be created

  @UI-SALE-004
  Scenario: Verify Delete confirmation popup
    Given user is logged in as "admin"
    When user opens sales page
    And user clicks Delete on a sale
    Then delete confirmation popup should be visible

  @UI-SALE-005
  Scenario: Verify plant validation (Empty Plant)
    Given user is logged in as "admin"
    When user opens sales page
    And user clicks Add Sale
    And user leaves plant empty
    And user enters quantity "5"
    And user saves sale
    Then validation error should be shown

  @UI-SALE-006
  Scenario: Verify user cannot see delete button
    Given user is logged in as "user"
    When user opens sales page
    Then Delete button should not be visible

  @UI-SALE-007
  Scenario: Verify user can sort sales by Sold Date
    Given user is logged in as "user"
    When user opens sales page
    And user sorts sales by "Sold Date"
    Then sales should be sorted by Sold Date

  @UI-SALE-008
  Scenario: Verify pagination for user
    Given user is logged in as "user"
    And user ensures there are at least 11 sales records
    When user opens sales page
    And user navigates to the next page
    Then the next set of records should be displayed

  @UI-SALE-009
  Scenario: Verify no sales message
    Given user is logged in as "user"
    When user opens sales page
    Then "No sales found" message should be displayed if list is empty

  @UI-SALE-010
  Scenario: Verify Sales menu active
    Given user is logged in as "user"
    Then Sales menu should be visible and active