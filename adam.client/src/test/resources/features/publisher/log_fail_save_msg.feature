# When not using the ADAM client to publish messages, it is possible that
# senders create a malformed JSON message and put them in the JMS queue
# ADAM is consuming messages from. Such messages will likely fail to create
# entries in the Event Log database and would have been removed from the JMS
# queue by ADAM, leaving no traces of the failure other than in the ADAM
# server event logs. Under these circumstances,
Feature: Log any failure to save an event in the database as an event
  As a System Administrator
  I want to know if messages failed to be persisted to the database
  So that a corrective action can be taken.

  Scenario: Log failure to persist message due to incorrect message format
    Given a publisher config file exists
    Given the message broker details are known
    Given an malformed event has been created
    When the event is published not using the ADAM client
    Then an error entry is created in the data source
    Then the event type is an ERROR
    Then the event description indicates a failure when persisting the message
    Then the event description contains the malformed message
    Then the event information contains the error stack trace

