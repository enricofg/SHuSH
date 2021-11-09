Feature: As a User 
I want to see a warning about activating my GPS
So that I can allow my location to be used

  Scenario: Confirm GPS Use
  Given I have click the share button
  When I have waited a few seconds
	Then my location services is turned off
	Then the app asks me to turn my location services on