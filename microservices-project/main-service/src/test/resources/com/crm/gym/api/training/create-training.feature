@SmokeTest
Feature: Create Training Session

  @PositiveTest
  Scenario: Create a new training session
  Given the following training session details:
  | Training type | Training name | Training date | Training duration | Trainer username | Trainee username |
  | Fitness       | Yoga Basics   | 2025-11-10    | 60                | John.Doe         | Jane.Doe         |
  When a request is made to create the training session
  Then the response should be Created

  @NegativeTest @ErrorHandlingTest
  Scenario: Reject training session creation with missing information
  Given the following training session details:
  | Training type | Training name | Training date | Training duration | Trainer username | Trainee username |
  | N/A           | Yoga Basics   | N/A           | 60                | N/A              | Jane.Doe         |
  When a request is made to create the training session
  Then the response should be Bad Request