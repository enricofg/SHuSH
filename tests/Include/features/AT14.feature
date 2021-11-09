Feature: As a User 
I want to be able to visualize data of past recordings 
So that I can understand the amount of noise I've been exposed to

Scenario: Graphics page has content
Given I have clicked the menu button
When I click the Gráfico navigation link
Then the app redirects to the graphics of past recording data screen
And the page has content
