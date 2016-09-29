package io.cify.runner.tasks

import io.cify.runner.utils.PluginExtensionManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * This task is responsible for triggering parameter parsing
 *
 * Created by FOB Solutions
 */
class CifyExtensionTask extends DefaultTask {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('CIFY EXTENSION TASK') as Marker

    @TaskAction
    void exec() {
        try {
            LOG.debug(MARKER, this.getName() + " started")

            new PluginExtensionManager(project).setupParameters()

            LOG.debug(MARKER, this.getName() + " finished")
        } catch (all) {
            LOG.error(MARKER, "Failed to execute " + this.getName(), all)
            throw all
        }

    }

}

