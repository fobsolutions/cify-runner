package io.cify.runner.tasks

import io.cify.runner.utils.Capabilities
import io.cify.runner.utils.TaskPoolManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * This task is responsible for collecting all tasks together and triggering them in parallel
 *
 * Created by FOB Solutions
 */

class CifyTask extends DefaultTask {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('CIFY TASK') as Marker


    @TaskAction
    void exec() {
        try {
            LOG.debug(MARKER, this.getName() + " started")

            TaskPoolManager taskPoolManager = new TaskPoolManager(project)

            List features = project.cify.features
            List capabilitiesSet = project.cify.capabilitiesSet
            String record = project.cify.record
            String videoPath = project.cify.videoPath

            features.each { String filePath ->
                File featureFile = new File(filePath)
                String featurePath = featureFile.path.replace(project.rootDir.toString() + '/', '')
                String featureName = featureFile.name

                capabilitiesSet.each { Capabilities capabilities ->
                    String taskName = featureName + capabilities.toString()

                    Map params = [:]
                    params.put('taskName', taskName)
                    params.put('featurePath', featurePath)
                    params.put('capabilities', capabilities)
                    params.put('record', record)
                    params.put('videoPath', videoPath)

                    taskPoolManager.addTask(taskName, CifyCucumberTask, params)
                }
            }
            taskPoolManager.runTasksInParallel(project.cify.threads as Integer)

            LOG.debug(MARKER, this.getName() + " finished")
        }
        catch (all) {
            LOG.error(MARKER, "Failed to execute " + this.getName(), all)
            throw (all)
        }
    }
}
