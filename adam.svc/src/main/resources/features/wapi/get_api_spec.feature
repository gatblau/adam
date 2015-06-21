Feature: Get the specification for the Web API
  As a developer intending to use ADAM's Web API
  I want to know its functional and technical specification
  So that I can perform calls to such API

  Scenario: Retrieve API Specification
    When the specification for the API is requested
    Then the specification is retrieved