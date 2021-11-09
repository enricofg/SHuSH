Feature: As a User
  I want to have the sound detection running
  So that I can share my sound recording info

  Scenario: Sound detection is off message
    Given the sound detection is turned off
    When I haved click the share button
    Then the app warns me the sound detection is stopped
