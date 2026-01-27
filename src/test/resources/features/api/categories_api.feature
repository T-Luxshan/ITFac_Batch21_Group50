Feature: Category Management (API)

  Scenario: Verify GET /api/categories returns categories list
    Given admin is authenticated via API
    When admin requests categories list
    Then API should return categories successfully
