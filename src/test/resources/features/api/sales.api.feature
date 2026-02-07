Feature: Sales Management API (Group50)

  @API-SALE-001
  Scenario: API-SALE-001 Verify DELETE /api/sales/{id} removes a record
    Given sales api is authenticated as "admin"
    When sales api user creates sale for plant id 1 with quantity 10
    When sales api user deletes the created sale
    Then sales api response status should be 204
    When sales api user sends GET for the deleted sale
    Then sales api response status should be 404

  @API-SALE-002
  Scenario: API-SALE-002 Verify Validation for Negative Quantity
    Given sales api is authenticated as "admin"
  When sales api user creates sale for plant id 1 with quantity -5
    Then sales api response status should be 400

  @API-SALE-003
  Scenario: API-SALE-003 Verify Validation for Non-Existent Plant ID
    Given sales api is authenticated as "admin"
    When sales api user creates sale for plant id 99999 with quantity 1
    Then sales api response status should be 404

  @API-SALE-004
  Scenario: API-SALE-004 Verify Sorting Parameters in GET Request
    Given sales api is authenticated as "admin"
    When sales api user requests sales with sort "quantity,desc"
    Then sales api response status should be 200
    And sales api response should be sorted by "quantity" in "desc" order

  @API-SALE-005
  Scenario: API-SALE-005 Verify Pagination Parameters in GET Request
    Given sales api is authenticated as "admin"
    When sales api user requests sales page 0 size 5
    Then sales api response status should be 200
    And sales api response should contain pagination metadata with size 5

  @API-SALE-006
  Scenario: API-SALE-006 Verify User can Retrieve Sales List
    Given sales api is authenticated as "user"
    When sales api user sends GET "/api/sales"
    Then sales api response status should be 200
    And sales api response should contain a list of sales

  @API-SALE-007
  Scenario: API-SALE-007 Verify User cannot UPDATE a sale (PUT)
    Given sales api is authenticated as "admin"
    Given sales api is authenticated as "user"
    When sales api user updates the created sale with quantity 50
    Then sales api response status should be 500

  @API-SALE-008
  Scenario: API-SALE-008 Verify User cannot view Admin-only fields (Data Leakage)
    Given sales api is authenticated as "user"
    When sales api user sends GET "/api/sales"
    Then sales api response status should be 200
    And sales api response should not contain sensitive fields like "costPrice"

  @API-SALE-009
  Scenario: API-SALE-009 Verify User cannot Delete Sale
    Given sales api is authenticated as "user"
    When sales api user creates sale for plant id 1 with quantity 1
    Then sales api response status should be 403

  @API-SALE-010
  Scenario: API-SALE-010 Verify Access without Token (Unauthenticated)
    Given sales api is not authenticated
    When sales api user requests sales page 0 size 5
    Then sales api response status should be 401
