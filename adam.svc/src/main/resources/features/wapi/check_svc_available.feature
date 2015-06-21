Feature: Check that the service is available
  As a System Administrator
  I want to have the means to check if ADAM's Web Service is available
  So that I can take a corrective action if not.

  Scenario: Check that the service is working
    When a request to the service root is made
    Then an OK message is received