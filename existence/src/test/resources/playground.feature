Feature: Year Widget

  Background:
    Given I open the page

  Scenario: I can submit the form
    Given I enter the year "1990"
    When I submit the form
    Then I see timeout