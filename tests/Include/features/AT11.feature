Feature:As a User

I want to press the stop button

So that I can save the recording data locally


Scenario:Local data storage 
Given I clicked the stop button
When the confirmation window asks me if I want to store locally the information
Then I click Sim
And the app stores the information
And I close the application
