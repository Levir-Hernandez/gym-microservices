@SmokeTest
Feature: Delete Trainee User

@PositiveTest
Scenario: Delete an existing trainee
Given a trainee named "John.Doe" exists
When a request is made to delete the trainee "John.Doe"
Then the response should be No Content
And the trainee should no longer exists

@NegativeTest
Scenario: Reject deletion of a non-existent trainee user
When a request is made to delete the trainee "John.Doe"
Then the response should be Not Found