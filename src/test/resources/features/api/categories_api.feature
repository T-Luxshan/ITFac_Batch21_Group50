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

  @TC_API_CAT_005
  Scenario: TC_API_CAT_005 Verify user cannot delete category API
    Given api user is authenticated as "user"
    When user deletes category id 1
    Then response status should be 403