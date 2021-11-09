Feature: As a User
  I want to be able to see the sound level
  So that I can know what the current danger level is

  Scenario: Sound danger level successful
    Given I have started the audio detection
    Then Verify that the Nível label value is changing
    And I close the application
