Feature: Delete event by Id
  As a System Administrator
  I want to delete a specific event in the log using its Id
  So that I can remove the unwanted entry.

  Scenario: Delete existing event using its Id
    Given an event exists in the data source
    When a request to delete the event is issued
    Then the event is deleted from the data source