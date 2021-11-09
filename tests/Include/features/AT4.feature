Feature: As a user
I want to stop sound detection
So that I can see the statistics of the detected sound levels



Scenario: Check if i can pause the recording
					Given I have started the audio detection
					And I have waited a few seconds
					When I toggle the "OFF/ON" button
					And I verify the button is off
					Then I verify that the "DB" label value stops changing
					And I close the application





