@SmokeTest
Feature: Get Training Sessions

  @PositiveTest
  Scenario: Retrieve all training sessions when trainings exist
  Given multiple training sessions exist
  When a request is made to retrieve all training sessions
  Then the response should be OK
  And the response should include a list of all training sessions

  @NegativeTest
  Scenario: Retrieve all training sessions when no trainings exist
  Given no training sessions exist
  When a request is made to retrieve all training sessions
  Then the response should be OK
  And the response should include an empty list of training sessions

  @PositiveTest
  Scenario: Retrieve trainings assigned to a trainer
  Given the trainer "John.Doe" has trainings assigned
  When a request is made to retrieve trainings by trainer username "John.Doe"
  Then the response should return all trainings assigned to trainer "John.Doe"

  @NegativeTest
  Scenario: Retrieve an empty list when trainer has no trainings assigned
  Given the trainer "John.Doe" has no trainings assigned
  When a request is made to retrieve trainings by trainer username "John.Doe"
  Then the response should include an empty list of training sessions

  @PositiveTest
  Scenario: Retrieve trainings assigned to a trainee
  Given the trainee "Alice.Smith" has trainings assigned
  When a request is made to retrieve trainings by trainee username "Alice.Smith"
  Then the response should return all trainings assigned to trainee "Alice.Smith"

  @NegativeTest
  Scenario: Retrieve an empty list when trainee has no trainings assigned
  Given the trainee "Alice.Smith" has no trainings assigned
  When a request is made to retrieve trainings by trainee username "Alice.Smith"
  Then the response should include an empty list of training sessions