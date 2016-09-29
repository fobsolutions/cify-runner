package io.cify.runner.utils

import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger

/**
 * This class is responsible for handling capabilities
 *
 * Created by FOB Solutions
 */

class CapabilityParser {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger

    private static final String ANDROID = "android"
    private static final String IOS = "ios"
    private static final String BROWSER = "browser"
    private static final String SET = "set"
    private static final String DEFAULTS = "defaults"

    /**
     * Gets full capabilities list from file or default list
     *
     * @return list of capabilities
     * */
    public
    static List generateCapabilitiesList(String capabilitiesFilePath, String extraCapabilities, String farmUrl, String capabilitiesFromCmd) {

        LazyMap capabilitiesContent
        if (!capabilitiesFromCmd.isEmpty()) {
            capabilitiesContent = new JsonSlurper().parseText(capabilitiesFromCmd) as LazyMap
        } else {
            capabilitiesContent = getCapabilitiesFromFile(capabilitiesFilePath.toString())
        }


        List capabilities = getCapabilitiesVariations(capabilitiesContent)
        LazyMap extraCapabilitiesMap = parseExtraCapabilities(extraCapabilities.toString())

        if (!farmUrl.isEmpty()) {
            extraCapabilitiesMap.put("remote", farmUrl)
        }

        List capabilitiesList = getCapabilitiesList(capabilities, extraCapabilitiesMap)

        LOG.debug("Capabilities list is: " + capabilitiesList.toArray())

        return capabilitiesList
    }

    /**
     * Gets all variations from capabilities file content
     *
     * @param capabilitiesFileContent - capabilities file content
     * @return list of capabilities variations
     * */
    public static List getCapabilitiesVariations(LazyMap capabilitiesFileContent) {
        List iOSCapabilitiesList = getCapabilitiesForType(capabilitiesFileContent, IOS)
        List browserCapabilitiesList = getCapabilitiesForType(capabilitiesFileContent, BROWSER)
        List androidCapabilitiesList = getCapabilitiesForType(capabilitiesFileContent, ANDROID)

        List capabilitiesSet = []


        for (int i = 0; i < iOSCapabilitiesList.size(); i++) {
            LazyMap ios = iOSCapabilitiesList.get(i) as LazyMap
            for (int a = 0; a < androidCapabilitiesList.size(); a++) {
                LazyMap android = androidCapabilitiesList.get(a) as LazyMap
                for (int b = 0; b < browserCapabilitiesList.size(); b++) {
                    LazyMap browser = browserCapabilitiesList.get(b) as LazyMap
                    Capabilities capability = new Capabilities()
                    capability.setIos(ios)
                    capability.setAndroid(android)
                    capability.setBrowser(browser)

                    capabilitiesSet.add(capability)
                }
            }
        }
        return capabilitiesSet
    }

    /**
     * Gets capability for device type
     *
     * @param capabilityFileContent - Capabilities json file content
     * @type - device type
     * */
    public static List getCapabilitiesForType(LazyMap capabilityFileContent, String type) {
        if (capabilityFileContent.get(SET) != null && capabilityFileContent.get(SET)[type] != null) {
            return capabilityFileContent.get(SET)[type] as List
        } else if (capabilityFileContent.get(DEFAULTS) != null && capabilityFileContent.get(DEFAULTS)[type] != null) {
            List typeDefault = []
            typeDefault.add(capabilityFileContent.get(DEFAULTS)[type])
            return typeDefault
        } else {
            return [[:]]
        }
    }

    /**
     * Validates capabilities structure and gets capabilities
     *
     * @param capabilities - capability file content
     * @param extraCapabilities - parameters to add to every capability
     * @return list of capabilities
     * */
    public static List getCapabilitiesList(List capabilities, LazyMap extraCapabilities) {
        List capabilitiesSet
        try {
            capabilitiesSet = capabilities != null ? capabilities : []
            capabilitiesSet.each { Capabilities capabilitiesObject ->
                capabilitiesObject.addCapabilitiesToAll(extraCapabilities)
            }
        } catch (all) {
            throw new CifyPluginException("Cannot get capabilitiesList from map cause: " + all.message, all)
        }
        return capabilitiesSet
    }

    /**
     * Gets capability file content
     *
     * @param capabilitiesFilePath
     * @return List
     * */
    public static LazyMap getCapabilitiesFromFile(String capabilitiesFilePath) {
        LazyMap capabilitiesMap = [:]
        try {
            if (capabilitiesFilePath.startsWith("http://")
                    || capabilitiesFilePath.startsWith("https://")) {
                LOG.info("Reading capability content from URL")
                capabilitiesMap = readFromContent(capabilitiesFilePath.toURL().text) as LazyMap
            } else {
                LOG.info("Reading capability content from File")
                File capabilitiesFile = new File(capabilitiesFilePath);
                if (capabilitiesFile.exists()) {
                    LOG.debug("Found capabilities file with name: " + capabilitiesFile.getName())
                    capabilitiesMap = readFromContent(capabilitiesFile.text) as LazyMap
                } else {
                    LOG.debug("Cannot find capabilities file")
                }
            }
        } catch (all) {
            throw new CifyPluginException("Parsing capabilities file content failed with message: " + all.message, all)
        }
        return capabilitiesMap
    }

    /**
     * Reads content from capabilities file
     *
     * @param text - capabilities file content in string format
     * @return LazyMap with capabilities
     * */
    public static Object readFromContent(String text) {
        try {
            JsonSlurper jsonParser = new JsonSlurper()
            def content = jsonParser.parseText(text)
            LOG.info("Read capability file with content: " + content)
            return content
        } catch (all) {
            throw new CifyPluginException("Cannot read capability file content cause: " + all.message, all)
        }
    }

    /**
     * Gets external parameters for desired capabilities
     *
     * @return map of capabilities
     * */
    public static LazyMap parseExtraCapabilities(String extraCapabilities) {

        LazyMap result

        if (extraCapabilities.isEmpty()) {
            result = [:]
        } else {
            result = extraCapabilities.split('&').inject([:]) { map, token ->
                token.split('=').with { map[it[0]] = it[1] }
                map
            } as LazyMap
        }

        return result
    }
}
