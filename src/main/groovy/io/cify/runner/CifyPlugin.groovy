package io.cify.runner

import io.cify.runner.tasks.CifyExtensionTask
import io.cify.runner.tasks.CifyTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Cify plugin.
 *
 * Created by FOB Solutions
 */
class CifyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("cify", CifyPluginExtension)

        project.task('parameters', type: CifyExtensionTask) {
            group = 'Cify'
            description = 'Reads and validates parameters, and saves them to a configuration file'
        }

        project.task('cucumber', type: CifyTask, dependsOn: ["build", project.tasks.parameters]) {
            group = 'Cify'
            description = 'Parses feature files, generates and executes Cucumber tasks'
        }

        project.task('help') {
            group = 'Cify'
            description = 'Shows a list of available options'

            doFirst {
                println(project.cify.helpText)
            }
        }
    }
}
