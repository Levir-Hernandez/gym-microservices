@SmokeTest
Feature: Delete Training Session

  @PositiveTest
  Scenario: Delete an existing training session
  Given a training session named "Yoga Basics" exists
  When a request is made to delete the training session "Yoga Basics"
  Then the response should be No Content

  @NegativeTest @ErrorHandlingTest
  Scenario: Delete non-existent training session
  Given no training session named "Yoga Basics" exists
  When a request is made to delete the training session "Yoga Basics"
  Then the response should be Not Found