Feature: Query Events
  As a System Administrator
  I want to query events by type and date range and be able to limit the result set
  So that I can inspect the event log.

  Scenario: Query Events by type and date range limiting result size
    Given there are many events in the event log
    When a query is issued specifying event type, date range and limit
    Then a query result is obtained