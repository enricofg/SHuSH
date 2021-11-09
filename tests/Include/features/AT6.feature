Feature: As a User
  I want to be able to pause the recording
  So that I can resume it later

  Scenario: Pause sound successful.
    Given I have started the audio detection
    When I click the pause button
    Then I verify that the "DB" label value stops changing
    And I verify that the "Maximo" label value stops changing
    And I verify that the "Minimo" label value stops changing
    And I verify that the "Mediana" label value stops changing
    And I close the application

  Scenario: Pause button integrity
    Given I have opened the application
    And the pause button is grayed out
    When I start the sound detection
    Then the pause button turns to blue
