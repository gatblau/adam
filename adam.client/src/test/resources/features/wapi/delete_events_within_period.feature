Feature: Delete all events within a time period
  As a System Administrator
  I want to delete events that occurred within a specific time period
  So that I can manage the size and content of the log.

  Scenario: Delete events within period
    Given there are many events in the event log
    When a request to delete all events between two dates
    Then the event log does not have any event within the specified dates