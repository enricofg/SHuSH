Feature: As a User 
I want to press a button on the navigation menu
So that I can be redirected to a noise map screen

  Scenario: Map screen redirection successful
	Given I have clicked the menu button
	When I clicked the Map navigation link
	Then the app redirects me to the shared noise data screen