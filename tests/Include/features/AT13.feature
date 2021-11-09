Feature: As a User
I want to press a button on the navigation menu
So that I can be redirected to a graphics screen

  Scenario: Graphics screen redirection successful
	Given I have clicked the menu button
	When I clicked the Graphics navigation link
	Then the app redirects me to the graphics of past recording data screen