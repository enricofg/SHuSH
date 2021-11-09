Feature: As a User
  I want to be able to click a button on the dashboard
  So that I can navigate to other features

  Scenario: Navigation menu integrity
    Given I have opened the application
    When I clicked the menu button
    Then the menu shows the available navigation pages
