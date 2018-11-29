package io.cify.runner.utils

import groovy.json.internal.LazyMap
import io.cify.runner.CifyPlugin
import io.cify.runner.CifyPluginExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static io.cify.runner.utils.CapabilityParser.*

/**
 * Created by FOB Solutions
 */
class CapabilityParserTest extends GroovyTestCase {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    private File capabilitiesFile

    Project project
    PluginExtensionManager manager
    CifyPlugin plugin

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
        plugin = new CifyPlugin()
        testProjectDir.create()
        capabilitiesFile = testProjectDir.newFile(new CifyPluginExtension().capabilitiesFilePath)
        capabilitiesFile.write(capabilities)
        project = ProjectBuilder.builder().build()
        plugin.apply(project)
        manager = new PluginExtensionManager(project)
    }

    void testGetCapabilities() {
        LazyMap capabilitiesMap = readFromContent(capabilities) as LazyMap
        List capabilities = getCapabilitiesVariations(capabilitiesMap)
        assert capabilities.size() == 3
    }

    void testGetCapabilitiesWithEmptyList() {
        List capabilities = getCapabilitiesList([], new LazyMap())
        assert capabilities.isEmpty()
    }


    void testReadCapabilitiesFromUrl() {
        String path = "http://www.fob-solutions.com/"
        shouldFail {
            getCapabilitiesFromFile(path)
        }
    }

    void testGetCapabilitiesWithIncorrectInput() {
        shouldFail {
            getCapabilitiesList(["test", "test"] as List, new LazyMap())
        }
    }

    void testGetCapabilitiesWithCustomParameters() {
        LazyMap lazyMap = readFromContent(capabilities) as LazyMap
        List capabilities = getCapabilitiesVariations(lazyMap)
        LazyMap extras = new LazyMap()
        extras.put("test", "testValue")
        List capabilitiesList = getCapabilitiesList(capabilities, extras)
        capabilitiesList.each { Capabilities capabilitiesObject ->
            assert capabilitiesObject.getIos().first().get("test") != null
            assert capabilitiesObject.getAndroid().first().get("test") != null
            assert capabilitiesObject.getBrowser().first().get("test") != null
            assert capabilitiesObject.getCustom().first().get("test") != null
        }
    }
}
