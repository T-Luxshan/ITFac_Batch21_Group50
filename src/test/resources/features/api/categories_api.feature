Feature: Category Management API (Group50)

  Scenario: TC_API_CAT_001 Verify GET categories API
   Given api user is authenticated as "admin"
   When user sends GET "/api/categories"
   Then response status should be 200

  @TC_API_CAT_002
  Scenario: TC_API_CAT_002 Verify create category API (Admin)
    Given api user is authenticated as "admin"
    When user creates unique category with name "Lilies"
    Then response status should be 201

  @TC_API_CAT_003
  Scenario: TC_API_CAT_003 Verify category name length validation
    Given api user is authenticated as "admin"
    When user creates category with name "AB"
    Then response status should be 400

  @TC_API_CAT_004
  Scenario: TC_API_CAT_004 Verify user cannot update category API
    Given api user is authenticated as "user"
    When user updates category id 1 with name "NewName"
    Then response status should be 403

  @TC_CAT_ADM_API_003 @admin @IFHAM
  Scenario: TC_CAT_ADM_API_003 - Get all sub-categories endpoint works
    Given api user authenticates as "admin"
    When api user sends GET to "/api/categories/sub-categories"
    Then api response status should be 200
    And api response should contain sub-categories list

  @TC_CAT_ADM_API_004 @admin @IFHAM
  Scenario: TC_CAT_ADM_API_004 - Admin can delete category successfully
    Given api user authenticates as "admin"
    And api user creates a new category for deletion
    When api user sends DELETE to delete the category
    Then api response status should be 204
    And category should be removed from system