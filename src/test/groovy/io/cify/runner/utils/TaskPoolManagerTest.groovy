package io.cify.runner.utils

import io.cify.runner.CifyPlugin
import io.cify.runner.tasks.CifyCucumberTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.testfixtures.ProjectBuilder

/**
 * Created by FOB Solutions
 */
class TaskPoolManagerTest extends GroovyTestCase {
    private Project project
    private CifyPlugin plugin
    private testTask = "testTask"
    private TaskPoolManager taskPoolManager
    public static final THREADCOUNT = 3

    void setUp() {
        plugin = new CifyPlugin();
        project = ProjectBuilder.builder().build();
        project.getPluginManager().apply('java')
        plugin.apply(project);
        project.task(testTask, type: CifyCucumberTask)
        taskPoolManager = new TaskPoolManager(project)
        new PluginExtensionManager(project).setupParameters()
    }

    void tearDown() {
        project.tasks.clear()
        TestTask.threadsNames.clear()
        TestTask.count = 0
    }

    void testTaskExists() {
        assert taskPoolManager.taskExists(testTask)
    }

    void testTaskDoNotExist() {
        assertFalse(taskPoolManager.taskExists("DoNotExist"))
    }

    void testAddTask() {
        String taskName = "testAddTask"
        taskPoolManager.addTask(taskName, CifyCucumberTask, ["browser": "chrome"])
        assert project.tasks[taskName] instanceof CifyCucumberTask
    }

    void testAddTaskThatExists() {
        String taskName = "testAddTask"
        taskPoolManager.addTask(taskName, CifyCucumberTask)
        taskPoolManager.addTask(taskName, CifyCucumberTask)
        taskPoolManager.tasksPool.size() == 1
    }

    void testRunInParallel() {
        addTestTasks(10)
        taskPoolManager.runTasksInParallel(THREADCOUNT)

        assert TestTask.threadsNames.size() == THREADCOUNT
    }

    void testRunCount() {
        addTestTasks(10)
        taskPoolManager.runTasksInParallel(1)
        assert TestTask.count == taskPoolManager.tasksPool.size()
    }

    void testEmptyTaskPool() {
        taskPoolManager.runTasksInParallel(THREADCOUNT)
        assert TestTask.threadsNames.isEmpty()
    }

    void testTaskPoolWithNegativeThreadsCount() {
        addTestTasks(5)
        shouldFail {
            taskPoolManager.runTasksInParallel(-4)
        }
    }

    void testWithReRunParameter() {
        project.ext.set('rerunFailedTests', 'true')
        new PluginExtensionManager(project).setupParameters()

        addFailingTask(2)
        taskPoolManager.runTasksInParallel(2)
    }

    /**
     * Adds tasks into task pool
     * */
    private void addTestTasks(int count) {
        for (int i = 0; i < count; i++) {
            taskPoolManager.addTask(i.hashCode() as String, TestTask, ['count': i.toString()])
        }
    }

    /**
     * Adds failing task to task pool
     * */
    private void addFailingTask(int count) {
        for (int i = 0; i < count; i++) {
            taskPoolManager.addTask(i.toString(), TestTask, ['fail': 'true'])
        }
    }
}

/**
 * Test task for testing parallel running
 * */
class TestTask extends DefaultTask {

    static List threadsNames = new ArrayList()
    static int count = 0
    Map<String, String> taskParams

    @TaskAction
    void exec() {
        ++count

        String name = Thread.currentThread().getName()
        if (!threadsNames.contains(name)) threadsNames.add(name)

        if (taskParams['fail'] == "true") {
            throw new CifyPluginException("Task failed")
        }
    }
}



