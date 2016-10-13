package io.cify.runner.tasks

import groovy.json.JsonOutput
import io.cify.runner.CifyPluginExtension
import io.cify.runner.Constants
import io.cify.runner.utils.CucumberArgsBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.gradle.api.tasks.JavaExec

/**
 * This task is responsible for passing right parameters to CucumberRunner
 *
 * Created by FOB Solutions
 */

class CifyCucumberTask extends JavaExec {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('CIFY CUCUMBER TASK') as Marker

    final static String JAVA_EXEC_MAIN = 'cucumber.api.cli.Main'

    public Map<String, Object> taskParams = [:]

    @Override
    void exec() {
        try {
            LOG.debug(MARKER, this.getName() + " started")

            args = getCucumberArgs(
                    project.cify as CifyPluginExtension,
                    taskParams['taskName'] as String,
                    taskParams['featurePath'] as String
            )
            classpath = project.configurations.testRuntime + project.sourceSets.test.output + project.sourceSets.main.runtimeClasspath
            main = JAVA_EXEC_MAIN

            systemProperties = [
                    'task'        : taskParams['taskName'],
                    'capabilities': JsonOutput.toJson(taskParams['capabilities']),
                    'videoRecord' : taskParams['videoRecord'],
                    'videoDir'    : taskParams['videoDir']
            ]

            System.properties.each { k, v ->
                if (k.toString().startsWith(Constants.CIFY_SYSTEM_PROPERTY_PREFIX)) {
                    String key = k.toString().replace(Constants.CIFY_SYSTEM_PROPERTY_PREFIX, "")
                    systemProperties.put(key, v)
                }
            }

            super.exec()

            LOG.debug(MARKER, this.getName() + " finished")
        } catch (all) {
            LOG.error(MARKER, "Failed to execute " + this.getName(), all)
            throw all
        }
    }

    static List getCucumberArgs(CifyPluginExtension cify, String taskName, String featurePath) {
        return new CucumberArgsBuilder(taskName)
                .addFeatureDir(featurePath ? featurePath : cify.featureDirs)
                .addTags(cify.tags)
                .addPlugins(cify.cucumberPlugins)
                .addGlue(cify.gluePackages)
                .setDryRun(cify.dryRun)
                .setStrict(cify.strict)
                .setMonochrome(cify.monochrome)
                .build()
    }
}