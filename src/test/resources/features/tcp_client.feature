Feature: Check the client communicates with Server

  @Tcp
  Scenario: Send command to server
    Given The Bouncer Server has started on port 6666
    And The Bouncer Client is started on port 6666
    When The Bouncer Server sends the ball position command
    Then The Bouncer Client will receive the command
    And The ball position command is executed
