package io.cify.runner.utils
/**
 * Created by FOB Solutions
 */
class CherkinFeatureParserTest extends GroovyTestCase {


    private final String GHERKIN =
            '''
    @all
    Feature: All applications and browsers tests

      As a tester I would like to open all applications and browsers
      in order to test them.

      @native
      Scenario Outline: User opens native application and clicks button
        Given user opens <application>
        When user clicks the button
        Then the button should be still visible

        Examples:
          | application |
          | Android app |
          | iOS app     |

      @web
      Scenario Outline: User opens web application and clicks button
        Given user opens <application>
        When user clicks the button
        Then the button should be still visible

        Examples:
          | application |
          | Chrome app  |
          | Safari app  |
        '''


    void testGherkinHasNoScenariosInBadFile() {
        shouldFail {
            GherkinFeatureParser.hasScenarios('no/file/here', ['@gherkin'])
        }
    }

    void testCountScenariosWithoutFilters() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', [])
        assert scenarios.size() == 2
    }

    void testCountScenariosFeatureTagFilter() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', ['@all'])
        assert scenarios.size() == 2
    }

    void testCountScenariosContainingTag() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', ['@native'])
        assert scenarios.size() == 1
    }

    void testCountScenariosContainingOneOfTags() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', ['@native, @web'])
        assert scenarios.size() == 2
    }

    void testCountScenariosContainingAllTags() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', ['@native', '@web'])
        assert scenarios.size() == 0
    }

    void testGherkinHasScenarios() {
        assert GherkinFeatureParser.hasScenarios(GHERKIN, '', [])
    }

    void testGherkinHasNoScenarios() {
        assert !GherkinFeatureParser.hasScenarios(GHERKIN, '', ['@gherkin'])
    }

}
