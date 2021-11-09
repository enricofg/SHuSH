Feature: As a User
  I want to be able to resume the recording
  So that I can continue from where I left off


  Scenario: Play button integrity
    Given I have started the audio detection
    And the play button is grayed out
    When I pause the sound detection
    Then the play button turns to green
