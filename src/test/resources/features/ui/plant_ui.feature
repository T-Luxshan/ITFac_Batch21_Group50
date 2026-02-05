Feature: Plant Management UI Tests

  @TC_PLANT_ADMIN_UI_001
  Scenario: Verify that admin can view the plant list page
    Given Login as Admin for Plant UI
    When Open Plant page
    Then Plant page should load
    And Plants table should display with name price quantity and category columns
    And Admin should be able to view all available plants

 @TC_PLANT_ADMIN_UI_002
   Scenario: Verify admin can add a new plant with valid data
     Given Login as Admin for Plant UI
     When Navigate to Plants page
     And Click on Add Plant button
     And Enter plant name "TestRose89"
     And Select category "fruit"
     And Enter price "500"
     And Enter stock quantity "10"
     And Click on Save button
     Then Plant is saved successfully
     And Success message is displayed
     And User is redirected to plant list page
     And Newly added plant "TestRose999" appears in the list

  @TC_PLANT_ADMIN_UI_003
  Scenario: Verify admin can edit plant details
    Given Login as Admin for Plant UI
    And At least one plant exists
    When Navigate to Plants page
    And Locate an existing plant
    And Click on Edit button
    And Change plant name to "Upda"
    And Change price to "7500"
    And Click Save
    Then Plant details are updated successfully
    And Updated values are visible in plant list
    And Success message is displayed

  @TC_PLANT_ADMIN_UI_004
  Scenario: Verify admin can delete a plant
    Given Login as Admin for Plant UI
    And At least one plant exists
    When Navigate to Plants page
    And Locate an existing plant
    And Click on Delete button for first plant
    Then Delete confirmation modal appears
    When Confirm the delete action
    Then Plant is deleted successfully
    And Deleted plant no longer appears in the plant list

  @TC_PLANT_ADMIN_UI_005
  Scenario: Verify validation message when admin enters invalid plant price
    Given Login as Admin for Plant UI
    When Navigate to Plants page
    And Click Add Plant
    And Enter valid plant name "Test Plant"
    And Enter negative price "-200"
    And Click on Save button
    Then Validation error message is displayed
    And Plant is not saved
    And User remains on Add Plant page

  @TC_PLANT_USER_UI_001
  Scenario: Verify user can view plant list
    Given Login as User for Plant UI
    When Navigate to Plants page
    Then Plant list page loads
    And Only active plants are visible
    And No admin actions are shown

  @TC_PLANT_USER_UI_002
  Scenario: Verify user can search plants by name
    Given Login as User for Plant UI
    When Navigate to Plants page
    And Enter plant name "Mango" in search box
    And Click Search
    Then Matching plants are displayed
    And Non-matching plants are hidden

  @TC_PLANT_USER_UI_003
  Scenario: Verify user cannot see Add Plant button
    Given Login as User for Plant UI
    When Navigate to Plants page
    Then Add Plant button is not visible

  @TC_PLANT_USER_UI_004
  Scenario: Verify user cannot access Add Plant page using direct URL
    Given Login as User for Plant UI
    When Manually enter "/ui/plants/add" in browser
    Then Access denied page is shown
    And User cannot add plants

  @TC_PLANT_USER_UI_005
  Scenario: Verify deleted plants are not visible to user
    Given Login as User for Plant UI
    And Deleted plant exists
    When Navigate to Plants page
    Then Deleted plants are not displayed
    And Only active plants are shown