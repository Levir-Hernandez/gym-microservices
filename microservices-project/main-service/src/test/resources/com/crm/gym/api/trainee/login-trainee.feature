@SmokeTest
Feature: Login Trainee User

  Background:
    Given the following trainees' credentials:
      | Username    | Password   |
      | John.Doe    | 0123456789 |
      | Alice.Smith | 9876543210 |
    And none of the trainees are locked out

  @PositiveTest
  Scenario: Trainee log in with valid credentials
    When a request is made to log in with trainee username "John.Doe" and password "0123456789"
    Then the response should be OK

  @NegativeTest
  Scenario: Trainee log in with invalid credentials
    When a request is made to log in with trainee username "Jack.Doe" and password "0123456789"
    Then the response should be Unauthorized

  @PositiveTest
  Scenario: Trainee change password with valid credentials
    When the following trainee change password request is made:
      | Username | Old password | New password |
      | John.Doe | 0123456789   | abcdefghij   |
    Then the response should be OK

  @NegativeTest
  Scenario: Trainee change password with invalid credentials
    When the following trainee change password request is made:
      | Username | Old password | New password |
      | Jack.Doe | 0123456789   | abcdefghij   |
    Then the response should be Unauthorized

  @SecurityTest
  Scenario Outline: Trainee brute-force protection after three failed logins
    When a request is made to log in with trainee username "<Username>" and password "<Password>"
    Then the response should be <Response>
    Examples:
      | Username | Password   | Response     |
      #-----------Before protection----------#
      | John.Doe | 0123456789 | OK           |
      #----------Trigger protection----------#
      | John.Doe | 0000000000 | Unauthorized |
      | John.Doe | 1111111111 | Unauthorized |
      | John.Doe | 2222222222 | Unauthorized |
      #-----------After protection-----------#
      | John.Doe | 0123456789 | Forbidden    |