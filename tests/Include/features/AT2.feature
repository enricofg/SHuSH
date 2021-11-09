Feature: Check Dashboard
As a user I want to open the application
And start the sound detection

Scenario: Verify the sound detection starts
	Given I have opened the application
	When I toggle the "OFF/ON" button
	And I verify the button is on
	Then I verify that the "DB" label value is changing
	And I close the application


Scenario: On/Off integrity
			Given I have started the audio detection
			And I verify the button is on
			When I toggle the "OFF/ON" button
			Then I verify the button is off
 