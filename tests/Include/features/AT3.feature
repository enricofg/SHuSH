Feature: As a user
I want to start the sound detection
So that I can see the statistics of the detected sound levels



Scenario: Statistics measurement successful
					Given I have opened the application
					When I toggle the "OFF/ON" button
					And I verify the button is on
					Then I verify that the "Maximo" label value is changing
					And I verify that the "Minimo" label value is changing
					And I verify that the "Mediana" label value is changing
					Then I close the application

 
