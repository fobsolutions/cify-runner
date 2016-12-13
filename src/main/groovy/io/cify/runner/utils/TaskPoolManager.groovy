package io.cify.runner.utils

import groovyx.gpars.GParsPool
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * This manager is responsible for handling pool of tasks
 *
 * Created by FOB Solutions
 */

class TaskPoolManager {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('TASK POOL MANAGER') as Marker

    Project project
    List tasksPool = []

    TaskPoolManager(Project project) {
        this.project = project
    }

    /**
     * Checks if task already exists in project
     *
     * @param taskName task name to check
     *
     * @return boolean
     * */
    boolean taskExists(String taskName) {
        LOG.debug(MARKER, "Check if task $taskName already exists in the project")

        if (project.tasks.findByName(taskName) != null) {
            LOG.debug(MARKER, "Task $taskName already exists in the project")
            return true
        } else {
            LOG.debug(MARKER, "No task $taskName found in the project")
            return false
        }
    }

    /**
     * Adds task to task pool
     *
     * @param taskName task name to add
     * @param task task type to add
     * */
    void addTask(String taskName, Class task, Map<String, String> params) {
        LOG.debug(MARKER, "Add task $taskName $task with parameters $params to task pool")

        if (!taskExists(taskName) || !task instanceof Task) {
            project.task(taskName, type: task) {
                taskParams = params
            }
            tasksPool.add(project.tasks[taskName])
        }
    }

    /**
     * Add task with empty params list
     * */
    void addTask(String taskName, Class task) {
        addTask(taskName, task, [:])
    }

    /**
     * Runs tasks in parallel with given thread count
     *
     * @param threadCount count to run in parallel
     * */
    void runTasksInParallel(int threadCount) {
        try {
            LOG.debug(MARKER, "Run tasks in parallel")
            LOG.debug(MARKER, "Task pool contains " + tasksPool.size() + " tasks")
            LOG.debug(MARKER, "Number of threads: " + threadCount)

            List failedTasks = []
            GParsPool.withPool(threadCount) {

                tasksPool.eachParallel {
                    try {
                        it.execute()
                    } catch (all) {
                        LOG.error(MARKER, "Failed to run task " + it.name + ". Cause: " + all.message)
                        failedTasks.add(it)
                    }
                }

                if (!failedTasks.isEmpty() && project.cify.rerunFailedTests) {

                    LOG.debug(MARKER, "Re-running failed cases")
                    LOG.debug(MARKER, "Failed task pool contains " + failedTasks.size() + " tasks")
                    LOG.debug(MARKER, "Number of threads: " + threadCount)

                    failedTasks.eachParallel {
                        try {
                            it.execute()
                        } catch (all) {
                            LOG.error(MARKER, "Re-running for task $it.name failed cause $all")
                        }
                    }
                }
            }
        } catch (all) {
            throw new CifyPluginException("TaskPoolManager: Exception occurred when executing tasks in parallel. Cause: " + all.message)
        }
    }
}
