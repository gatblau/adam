# This feature describes the act of publishing application events
Feature: Publish an event
  A software application
  requires to publish an event
  so that it is recorded for monitoring purposes

  # This scenario applies when key event information is provided as part of the created event.
  # This information includes the unique identifier for the event, the service raising the event and
  # the name of the node the service is running on.
  Scenario: Publish an event with explicit parameters
    Given a publisher config file exists
    Given the publisher config file defines the Message Broker URI
    Given the publisher config file defines the name of the Message Queue where events should be sent
    Given an event with specific Event Id, Service Name and Node Name has been created
    When the event is published
    Then an event entry is created in the data source
    Then the event parameters have been recorded in the data source

  # This scenario applies when key event information is stored in a configuration properties file
  # and does not need to be specified in the event message.
  # The publisher automatically populates Event Id, Service and Node information from configuration.
  # The Event Id is created using Service name and Time Stamp.
  Scenario: Publish an event with no explicit parameters
    Given a publisher config file exists
    Given the publisher config file defines the Message Broker URI
    Given the publisher config file defines the name of the Message Queue where events should be sent
    Given the publisher config file defines the name of the Service publishing the event
    Given the publisher config file defines the name of the Node where the Service publishing the event is running
    Given an event not having Event Id, Service Name and Node Name has been created
    When the event is published
    Then an event entry is created in the data source
    Then the event parameters have been recorded in the data source