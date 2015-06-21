Feature: Find an event by Id
  A software engineer
  wants to find an event
  so that event information can be used for development and defect fixing purposes.

  Scenario: Find an existing event by Id
    Given an event exists in the data source
    When the event is requested by Id
    Then the event invocation succeeds
    Then the event is retrieved

  Scenario: Try to find an event that does not exist by Id
    Given an event does not exist in the data source
    When the event is requested by Id
    Then the service invocation fails