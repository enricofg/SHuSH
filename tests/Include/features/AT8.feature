Feature:As a User
					I want to be able to see the recording level background change color
					So that I can be visually aware of the sound danger level

					
Scenario:Circle background color change successful
Given I have started the audio detection
Then I verify that the circle background color is changing 
And I close the application
