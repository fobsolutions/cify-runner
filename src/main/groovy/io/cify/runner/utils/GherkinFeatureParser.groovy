package io.cify.runner.utils

import gherkin.formatter.FilterFormatter
import gherkin.formatter.JSONFormatter
import gherkin.parser.Parser
import gherkin.util.FixJava
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger

/**
 * This parser is responsible for reading and filtering Gherkin files
 *
 * Created by FOB Solutions
 */

class GherkinFeatureParser {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('GHERKIN PARSER') as Marker

    /**
     * Checks if feature file has scenarios
     *
     * @param filePath a path to feature file
     * @param filters list of filters to apply on gherkin string
     * @return boolean
     * */
    static boolean hasScenarios(String filePath, List filters) {
        LOG.debug(MARKER, "Check for scenarios matching filters $filters in feature file $filePath")
        String gherkin = readFile(filePath)
        gherkin ? hasScenarios(gherkin, filePath, filters) : false
    }

    /**
     * Checks if gherkin string has scenarios
     *
     * @param gherkin a gherkin string
     * @param filePath a path to feature file
     * @param filters filters to apply on gherkin string
     *
     * @return boolean
     * */
    static boolean hasScenarios(String gherkin, String filePath, List filters) {
        LOG.debug(MARKER, "Check for scenarios matching filters $filters in gherkin ($filePath) \n $gherkin")
        getScenarios(gherkin, filePath, filters)
    }

    /**
     * Gets scenarios/scenario outlines in gherkin
     *
     * @param gherkin a gherkin string
     * @param filePath a path to feature file
     * @param filters filters to apply on gherkin string
     * @return the number of scenarios in gherkin
     * */
    static List<Object> getScenarios(String gherkin, String filePath, List filters) {
        List<Object> result = []
        Object json = gherkinToJson(gherkin, filePath, filters)
        if (json && json.elements && json.elements.size() > 0) {
            result = filterScenarios(json,filters).elements
        }
        result
    }

    private static Object filterScenarios(Object json, List filters){
        //println('filterScenarios started')
        List negative = []
        List positive = []
        int positiveFilterSize = 0
        boolean exclude = false
        def obj = json.get(0)

        if(! filters) {
            return obj
        }

        // fill filter lists
        filters[0].tokenize(',').each{
            //println('filter element: '+ it)
            if(it.toString().trim().startsWith('~@') ) {
                negative.add(it.trim().replace('~',''))}
            if(it.toString().trim().startsWith('@') ) {
                positive.add(it.trim())}
        }

        if(positive){
            positiveFilterSize = positive.size()
        }
        //println('positive filter size: '+ positiveFilterSize)

            // check if negative and positive filters have same values
            negative.each{
                if(positive.contains(it)){
                    positive.remove(it)
                }
            }

        //println('scenarios qty: '+ obj.elements.findAll{ it.keyword == 'Scenario' }.size())

            // iterate all scenarios
            List scenariosToRemove = []
            obj.elements.findAll{ it.keyword == 'Scenario' }.eachWithIndex { item,index ->
                int matched = 0
                int rootTagsSize = 0
                int tagsSize = 0
                boolean include = false
                exclude = false



                if(json.tags && json.tags[0]){
                    rootTagsSize = json.tags[0].size()
                }

                if(item.tags){
                    tagsSize = item.tags.size()
                }

                //println('index:'+index+ ' name: '+ item.name + ' tags: '+ item.tags + ' size:'+ tagsSize)
                //println('root tag size: ' + rootTagsSize + ' root tag: ' + json.tags[0])

                json.tags[0].each {
                    if(!exclude) {
                        //println('root tag: ' + it.name)
                        if (positive.contains(it.name)) {
                            matched++
                            include = true
                        }
                        if (negative.contains(it.name)) {
                            matched--
                            exclude = true
                            scenariosToRemove.add(index)
                        }
                    }
                }

                if(!exclude) {
                    item.tags.each {
                        if(!exclude) {
                            //println('scenario tag: ' + it.name)
                            if (positive.contains(it.name)) {
                                matched++
                                include = true
                            }
                            if (negative.contains(it.name)) {
                                matched--
                                exclude = true
                                scenariosToRemove.add(index)
                            }
                        }
                    }
                }
                //println('positive filter: '+ positive)
                //println('positive filter size: '+ positiveFilterSize)
                //println('rootTagsSize + tagsSize: '+ (rootTagsSize + tagsSize))
                //println('scenario tags matched:'+matched)
                if( !exclude && positiveFilterSize > 0 && positiveFilterSize > matched){
                    exclude = true
                    //println('scenario index to remove: '+ index)
                    scenariosToRemove.add(index)
                }

                // remove scenario which didn't matched positive filter
                if(!exclude && positiveFilterSize > 0 && !include){
                    exclude = true
                    scenariosToRemove.add(index)
                }

            }
            //println('old feature object scenarios qty:'+ obj.elements.size)
            //println('scenarios to remove: '+ scenariosToRemove)

            if(scenariosToRemove && scenariosToRemove.size() > 0) {
                int removed = 0
                scenariosToRemove.each {
                    //println('this will be removed: ' + obj.elements[it - removed])
                    obj.elements.remove(obj.elements[it - removed])
                    removed++
                }
            }

            //println('new feature object scenarios qty:'+ obj.elements.size)
            ////println('new feature object pretty: ' + new JsonBuilder(obj).toPrettyString())
            //println('new obj' + obj)

            return obj
    }

    private static String readFile(String filePath) {
        LOG.debug(MARKER, "Read file $filePath")
        return FixJava.readReader(new InputStreamReader(new FileInputStream(filePath), 'UTF-8'))
    }

    /**
     * Converts gherkin to JSON
     *
     * @param gherkin a gherkin string
     * @param featurePath a path to feature file
     * @param filters filters to apply on gherkin string
     * @return JSON object
     * */
    private static Object gherkinToJson(String gherkin, String featurePath, List filters) {
        LOG.debug(MARKER, "Parse gherkin to json. \n $gherkin \n $featurePath \n $filters")
        try {
            StringBuilder json = new StringBuilder()

            JSONFormatter formatter = new JSONFormatter(json)
            Parser parser = new Parser(formatter)

            if (filters) {
                FilterFormatter filterFormatter = new FilterFormatter(formatter, filters)
                parser = new Parser(filterFormatter)
            }

            parser.parse(gherkin, featurePath ?: '', 0)
            formatter.done()
            formatter.close()

            return new JsonSlurper().parseText(json.toString())
        } catch (all) {
            LOG.warn("Failed to parse gherkin to JSON: $featurePath. \n Cause: $all.message", all)
        }
        null
    }
}