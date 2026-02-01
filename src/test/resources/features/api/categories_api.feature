Feature: Category Management API (Group50)

  Scenario: TC_API_CAT_001 Verify GET categories API
   Given api user is authenticated as "admin"
   When user sends GET "/api/categories"
   Then response status should be 200

  @TC_API_CAT_002
  Scenario: TC_API_CAT_002 Verify create category API (Admin)
    Given api user is authenticated as "admin"
    When user creates category with name "Lilies"
    Then response status should be 201
