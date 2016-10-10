package io.cify.runner.utils

import io.cify.runner.CifyPlugin
import io.cify.runner.CifyPluginExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder

/**
 * Created by FOB Solutions
 */

class PluginExtensionManagerTest extends GroovyTestCase {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    private File propertiesFile
    private File capabilitiesFile
    private File fileWithMode

    Project project
    CifyPluginExtension defaultExtension
    CifyPluginExtension extension
    PluginExtensionManager manager

    String capabilities = "{\n" +
            "  \"defaults\": {\n" +
            "    \"android\": {\n" +
            "      \"version\": \"5.1\"\n" +
            "    },\n" +
            "    \"ios\": {\n" +
            "      \"version\": \"9.3\"\n" +
            "    },\n" +
            "    \"browser\": {\n" +
            "      \"version\": \"48\",\n" +
            "      \"type\": \"chrome\"\n" +
            "    }\n" +
            "  },\n" +
            "\n" +
            "  \"set\": {\n" +
            "    \"browser\": [\n" +
            "      {\n" +
            "        \"version\": \"44\",\n" +
            "        \"type\": \"safari\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"version\": \"12\",\n" +
            "        \"type\": \"opera\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"version\": \"6.0\",\n" +
            "        \"type\": \"android\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"android\": [\n" +
            "      {\n" +
            "        \"version\": \"6.0\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}"

    void setUp() {
        CifyPlugin plugin = new CifyPlugin()
        project = ProjectBuilder.builder().build()
        plugin.apply(project)
        manager = new PluginExtensionManager(project)
        manager.setupParameters()
        extension = project.cify
        defaultExtension = new CifyPluginExtension()
        testProjectDir.create()
        propertiesFile = testProjectDir.newFile("env-test.envProperties")
        capabilitiesFile = testProjectDir.newFile(extension.capabilitiesFilePath)
        fileWithMode = testProjectDir.newFile("fileWithMode.json")
    }

    void testGetDefaultFeatureDir() {
        assert extension.featureDirs == defaultExtension.featureDirs
    }

    void testDefaultThreadCount() {
        assert extension.threads == defaultExtension.threads
    }

    void testDefaultGlue() {
        assert extension.gluePackages == defaultExtension.gluePackages
    }

    void testDefaultTags() {
        assert extension.tags == defaultExtension.tags
    }

    void testDefaultPlugins() {
        assert extension.cucumberPlugins == defaultExtension.cucumberPlugins
    }

    void testDefaultDryRun() {
        assert extension.dryRun == defaultExtension.dryRun
    }

    void testDefaultStrict() {
        assert extension.strict == defaultExtension.strict
    }

    void testDefaultMonochrome() {
        assert extension.monochrome == defaultExtension.monochrome
    }

    void testDefaultIgnoreFailures() {
        assert extension.ignoreFailures == defaultExtension.ignoreFailures
    }

    void testDefaultCapabilities() {
        assert defaultExtension.capabilities.isEmpty()
    }

    void testDefaultRecording() {
        assert !defaultExtension.record.isEmpty()
        assert extension.record == defaultExtension.record
    }

    void testDefaultVideoPath() {
        assert !defaultExtension.videoPath.isEmpty()
        assert extension.videoPath == defaultExtension.videoPath
    }

    void testHelpText() {
        String helpText = extension.helpText
        assert !helpText.isEmpty()
    }

    void testGetValueFromPropertiesFile() {
        project.ext.set("env", "test")
        writeToProperties([
                "threads": "20"
        ])
        InputStream inputStream = manager.readPropertiesFromFile(propertiesFile)
        manager.envProperties.load(inputStream)
        assert manager.getValue("threads") == "20"
    }

    void testGetValueFromCommandLine() {
        project.ext.set("threads", "15")
        assert manager.getValue("threads") == "15"
    }

    void testEnvExistsButFileDont() {
        project.ext.set("env", "test")
        shouldFail(FileNotFoundException) {
            manager.setEnvProperties()
        }
    }

    void testBothCommandLineAndFileExist() {
        project.ext.set("env", "test")
        project.ext.set("threads", "15")
        writeToProperties([
                "threads": "20"
        ])
        InputStream inputStream = manager.readPropertiesFromFile(propertiesFile)
        manager.envProperties.load(inputStream)
        assert manager.getValue("threads") == "15"
    }

    void testWithMissingCapabilitiesFile() {
        List<Capabilities> capabilitiesSet = project.cify.capabilitiesSet
        assert capabilitiesSet.size() == 1
        assert capabilitiesSet.first().getAndroid().isEmpty()
        assert capabilitiesSet.first().getIos().isEmpty()
        assert capabilitiesSet.first().getBrowser().isEmpty()
    }

    void testWithCapabilities() {
        capabilitiesFile.write(capabilities)
        project.ext.set("capabilitiesFilePath", capabilitiesFile.getPath())
        manager.setupParameters()
        assert project.cify.capabilitiesSet.size() == 3
    }

    void testCapabilitiesFromCommandLine() {
        project.ext.set("capabilities", capabilities)
        manager.setupParameters()
        project.cify.capabilitiesSet.size() == 3
    }

    void testExternalParametersForCapability() {
        capabilitiesFile.write(capabilities)
        project.ext.set("capabilitiesFilePath", capabilitiesFile.getPath())
        project.ext.set("extraCapabilities", "test=test1Value&test2=test2Value")
        manager.setupParameters()
        List capabilitiesSet = project.cify.capabilitiesSet
        capabilitiesSet.each { Capabilities capabilities ->
            assert capabilities.getAndroid().get("test") == "test1Value"
            assert capabilities.getAndroid().get("test2") == "test2Value"
            assert capabilities.getIos().get("test") == "test1Value"
            assert capabilities.getIos().get("test2") == "test2Value"
            assert capabilities.getBrowser().get("test") == "test1Value"
            assert capabilities.getBrowser().get("test2") == "test2Value"
        }
    }

    void testWithRemoteUrl() {
        project.ext.set("farmUrl", "https://www.fob-solutions.com")
        manager.setupParameters()

        project.cify.capabilitiesSet.each { Capabilities capabilities ->
            capabilities.getBrowser().get("remote") == "https://www.fob-solutions.com"
            capabilities.getAndroid().get("remote") == "https://www.fob-solutions.com"

        }
    }

    void testWithCapabilityWithRemoteAndRemoteParam() {
        capabilitiesFile.write(capabilities)
        project.ext.set("capabilitiesFilePath", capabilitiesFile.getPath())
        project.ext.set("farmUrl", "https://www.fob-solutions.com")
        manager.setupParameters()
        project.cify.capabilitiesSet.each { Capabilities capabilities ->
            capabilities.getBrowser().get("remote") == "https://www.fob-solutions.com"
            capabilities.getAndroid().get("remote") == "https://www.fob-solutions.com"
        }
    }

    void testValidateTagsParameter() {
        shouldFail {
            project.ext.set("tags", "~@Broken,@Android,~Chrome")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("tags", "~@Broken,@Android,Chrome")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("tags", "~@Broken,@Android,@")
            manager.setupParameters()
        }
    }

    void testValidateDryRunParameters() {
        shouldFail {
            project.ext.set("dryRun", "truu")
            manager.setupParameters()
        }
    }

    void testValidateStrictParameters() {
        shouldFail {
            project.ext.set("strict", "truu")
            manager.setupParameters()
        }
    }

    void testValidateMonochromeParameters() {
        shouldFail {
            project.ext.set("monochrome", "truu")
            manager.setupParameters()
        }
    }

    void testValidateIgnoreFailuresParameters() {
        shouldFail {
            project.ext.set("ignoreFailures", "truu")
            manager.setupParameters()
        }
    }

    void testValidateThreadsParameter() {
        shouldFail {
            project.ext.set("threads", "four")
            manager.setupParameters()
        }
    }

    void testValidateCapabilitiesFilePathParameter() {
        project.ext.set("capabilitiesFilePath", defaultExtension.capabilitiesFilePath)
        manager.setupParameters()
        assert manager.getValue("capabilitiesFilePath") == defaultExtension.capabilitiesFilePath

        shouldFail {
            project.ext.set("capabilitiesFilePath", "src/testtt/resources/caps.json")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("capabilitiesFilePath", "src/test/resources")
            manager.setupParameters()
        }
    }

    void testValidateRemoteUrlParameter() {
        shouldFail {
            project.ext.set("farmUrl", "http:/www.here.ee")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("farmUrl", "httpss://www.here.ee")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("farmUrl", "www.here.ee")
            manager.setupParameters()
        }
    }

    void testValidateExtraCapabilitiesParameter() {
        shouldFail {
            project.ext.set("extraCapabilities", "param1:value1&param2:value2")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("extraCapabilities", "param1=value1,param2=value2")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("extraCapabilities", "param1=value1&param2=")
            manager.setupParameters()
        }
    }

    void testRecord() {
        project.ext.set("record", "true")
        manager.setupParameters()
        assert project.cify.record == "true"
    }

    void testRecordWithInvalid() {
        project.ext.set("record", "truu")
        shouldFail {
            manager.setupParameters()
        }
    }

    void testVideoPath() {
        project.ext.set("videoPath", "results/videos/")
        manager.setupParameters()
        assert project.cify.videoPath == "results/videos/"
    }

    private void writeToProperties(Map<String, String> propertiesMap) {
        Properties properties = new Properties()
        propertiesMap.each { k, v -> properties.setProperty(k, v) }

        propertiesFile.withWriterAppend('UTF-8') { fileWriter ->
            fileWriter.writeLine ''
            properties.each { key, value ->
                fileWriter.writeLine "$key=$value"
            }
        }
    }
}
