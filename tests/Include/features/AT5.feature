Feature: As a User
  I want to be able to stop the recording
  So that I can start again from zero

  Scenario: Reset sound successful.
    Given I have started the audio detection
    When I click the stop button
    Then I verify that the "DB" label is "0"
    And I verify that the "Maximo" label is "0"
    And I verify that the "Minimo" label is "0"
    And I verify that the "Mediana" label is "0"
    And I close the application

  Scenario: Stop button integrity
    Given I have opened the application
    And the stop button is grayed out
    When I start the sound detection
    Then the stop button turns to red
