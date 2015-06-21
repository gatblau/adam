# This scenario is useful to troubleshoot performance issues
# by querying all events that were sent at key points in a business transaction
# which might span different services and analysing their time stamps
Feature: Find events in a business process
  A software engineer
  wants to find all the events that were part of a business process
  so that the sequence of events can be analysed for performance or debugging purposes.

  Scenario: Find events by process Id
    Given a series of events exists in the data source for a given process
    When the events are requested using the process Id
    Then the events are retrieved

  Scenario: Try to find events for a process which does not exist
    Given there are no events in the data source for a given process
    When the events are requested using the process Id
    Then the events are not found