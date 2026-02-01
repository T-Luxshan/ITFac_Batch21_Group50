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
  
  Scenario: TC_CAT_003 Verify filtering by parent category works
    Given user is logged in as "admin"
    When user opens categories page
    And user filters by parent category "Flowers"
    Then results should show only children of "Flowers"
  
  Scenario: TC_CAT_004 Verify admin can add category with valid name
    Given user is logged in as "admin"
    When user opens categories page
    And user clicks Add Category
    And user enters category name "Lilies"
    And user saves category
    Then category "Lilies" should appear in the list
  
  @TC_CAT_005
  Scenario: TC_CAT_005 Verify normal user cannot add category
    Given user is logged in as "user"
    When user opens categories page
    Then Add Category button should not be visible
    When user opens add category page directly
    Then user should see access denied page

  @TC_CAT_006
  Scenario: TC_CAT_006 Verify category sorting by ID
    Given user is logged in as "admin"
    When user opens categories page
    And user sorts by "ID"
    Then categories should be sorted by ID

  @TC_CAT_007
  Scenario: TC_CAT_007 Verify category sorting by Name
    Given user is logged in as "admin"
    When user opens categories page
    And user sorts by "Name"
    Then categories should be sorted by Name

  @TC_CAT_008
  Scenario: TC_CAT_008 Verify category sorting by Parent Category
    Given user is logged in as "admin"
    When user opens categories page
    And user sorts by "Parent Category"
    Then categories should be grouped by parent category

  @TC_CAT_009
  Scenario: TC_CAT_009 Verify adding main category without parent
    Given user is logged in as "admin"
    When user opens add category page
    And user enters category name "MainCat"
    And user leaves parent category empty
    And user saves category
    Then category "MainCat" should be a main category