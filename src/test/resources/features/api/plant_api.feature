Feature: Plant API Tests
  Test the CRUD operations of Plant API for Admin and User roles, including unauthorized requests.


  Scenario: TC_API_PLANT_ADMIN_01 - Admin can get plant list
    Given I login as "admin" with password "admin123"
    When I send GET request to "/api/plants"
    Then the response status code should be 200

  Scenario: TC_API_PLANT_ADMIN_02 - Admin can create plant
    Given I login as "admin" with password "admin123"
    When I create a plant with name "Banana", categoryId 5, price 250, quantity 25
    Then the response status code should be 201

  Scenario: TC_API_PLANT_ADMIN_03 - Error when plant name is missing
    Given I login as "admin" with password "admin123"
    When I create a plant with name "", categoryId 5, price 100, quantity 20
    Then the response status code should be 400

  Scenario: TC_API_PLANT_ADMIN_04 - Admin can update plant
    Given I login as "admin" with password "admin123"
    When I update plant with id 6, name "Rose", categoryId 5, categoryName "tree", parentId 2, parentName "Lilies", price 260, quantity 25
    Then the response status code should be 200



  Scenario: TC_API_PLANT_ADMIN_05 - Admin can delete plant
    Given I login as "admin" with password "admin123"
    When I delete plant with id 4
    Then the response status code should be 204


  Scenario: TC_API_PLANT_USER_01 - User can get plant list
    Given I login as "testuser" with password "test123"
    When I send GET request to "/api/plants"
    Then the response status code should be 200

  Scenario: TC_API_PLANT_USER_02 - User cannot create plant
    Given I login as "testuser" with password "test123"
    When I create a plant with name "Sunflower", categoryId 5, price 150, quantity 10
    Then the response status code should be 403

  Scenario: TC_API_PLANT_USER_03 - User cannot update plant
    Given I login as "testuser" with password "test123"
    When I update plant with id 2, name "Orchid", categoryId 5, categoryName "tree", parentId 2, parentName "Lilies", price 60, quantity 15
    Then the response status code should be 403


  Scenario: TC_API_PLANT_USER_04 - User cannot delete plant
    Given I login as "testuser" with password "test123"
    When I delete plant with id 4
    Then the response status code should be 403

  Scenario: TC_API_PLANT_UNAUTHORIZED_01 - GET request without token
    When I send GET request without token to "/api/plants"
    Then the response status code should be 401

  Scenario: TC_API_PLANT_UNAUTHORIZED_02 - Create plant without token
    When I create plant without token
    Then the response status code should be 401
