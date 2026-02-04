Feature: Category Management API (Group50)

  @TC_API_CAT_001
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

  @TC_API_CAT_006
  Scenario: TC_API_CAT_006 Verify GET category by ID
    Given api user is authenticated as "admin"
    When user sends GET "/api/categories/1"
    Then response status should be 200

  @TC_API_CAT_007
  Scenario: TC_API_CAT_007 Verify GET category by invalid ID
    Given api user is authenticated as "admin"
    When user sends GET "/api/categories/999999"
    Then response status should be 404

  @TC_API_CAT_008
  Scenario: TC_API_CAT_008 Verify admin can update category API
    Given api user is authenticated as "admin"
    When user updates category id 1 with name "UpdatedCat"
    Then response status should be 200

  @TC_API_CAT_009
  Scenario: TC_API_CAT_009 Verify update validation error
    Given api user is authenticated as "admin"
    When user updates category id 1 with name "A"
    Then response status should be 500

  @TC_API_CAT_010
  Scenario: TC_API_CAT_010 Verify paginated category API
    Given api user is authenticated as "admin"
    When user requests category page 0 size 10 sort "name,asc"
    Then response status should be 200
