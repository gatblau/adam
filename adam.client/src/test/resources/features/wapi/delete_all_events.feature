Feature: Delete all events
  As a System Administartor
  I want to delete ALL events in the log
  So that I can all events that have been dealt with are not using unnecessary storage.

  Scenario: Delete all events
    Given there are many events in the event log
    When a request to delete all events is issued
    Then the event log does not have any event