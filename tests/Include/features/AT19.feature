Feature: As a User 
I want to see "-" as the default sound value

  Scenario: Default value is "-"
	Given the sound detection is turned off
	Then the default db level is '-'