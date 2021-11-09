Feature:As a User
I want to click a button that allows me to share the current noise value
So that I can share the noise value at my current place and time


Scenario: Confirm share information
Given I have clicked the share button
And the confirmation window asks me if I want to share the information
And the Ruído atual label has a value
And the Data label has a value
And the Localização label has a value
Then I click Sim
And I close the application

 
Scenario:Reject share information
Given I have clicked the share button
And the confirmation window asks me if I want to share the information
And the Ruído atual label has a value
And the Data label has a value
And the Localização label has a value
Then I click Não
And I close the application

Scenario:Information sharing integrity
Given I have clicked the share button
And the confirmation window asks me if I want to share the information
And the Ruído atual label has a value
And the Data label has a value
And the Localização label has a value
And I click Sim
And I verify that I cannot click the Sim button to share the same information again
And I close the application
