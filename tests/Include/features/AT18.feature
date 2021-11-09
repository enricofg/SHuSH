Feature: As a User
I want to be online
So that I can share my sound recording info

  Scenario: App offline message
    Given my internet data is turned off 
    When I haved click the share button
    Then the app warn me I have no data
    And I cannot share the noise detection information
