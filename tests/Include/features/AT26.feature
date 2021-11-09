Feature:As a user
I want to be able to receive notifications
So that I can know when the danger level in my zone is high

Scenario:Show notifications
Given that a loud noise has been recorded
And I have notifications ON
Then I can see a new notification related to the noise warning
