package io.cify.runner.utils

import org.apache.groovy.json.internal.LazyMap

import static io.cify.runner.utils.CapabilityParserNew.ANDROID
import static io.cify.runner.utils.CapabilityParserNew.BROWSER
import static io.cify.runner.utils.CapabilityParserNew.IOS

/**
 * Created by FOB Solutions
 *
 * Set of capabilities
 */
class Capabilities {

    private static final CAPABILITY_ID = "capabilityId"

    List<LazyMap> ios = []
    List<LazyMap> android = []
    List<LazyMap> browser = []

    /**
     * Add capability to every object
     */
    void addCapabilitiesToAll(LazyMap extraCapabilities) {
        browser.each {
            it.putAll(extraCapabilities)
        }
        android.each {
            it.putAll(extraCapabilities)
        }
        ios.each {
            it.putAll(extraCapabilities)
        }
    }

    /**
     * Adds capabilities to specified device category
     * @param deviceCategory capabilities device category
     * @param capabilities capabilities to be added
     */
    void addCapabilities(String deviceCategory, LazyMap capabilities) {
        switch (deviceCategory) {
            case IOS:
                ios.add(capabilities)
                break
            case ANDROID:
                android.add(capabilities)
                break
            case BROWSER:
                browser.add(capabilities)
                break
            default:
                throw new CifyPluginException("Unknown device category")
        }
    }

    /**
     * Generate unique string from capabilities object
     */
    @Override
    String toString() {
        String browserCapsId = getCapabilityIdentifier(getBrowser())
        String androidCapsId = getCapabilityIdentifier(getAndroid())
        String iosCapsId = getCapabilityIdentifier(getIos())

        browserCapsId = browserCapsId ? "_" + browserCapsId : ""
        androidCapsId = androidCapsId ? "_" + androidCapsId : ""
        iosCapsId = iosCapsId ? "_" + iosCapsId : ""

        return browserCapsId + androidCapsId + iosCapsId
    }

    /*
     * Generates class data representation string
     */
    String toPrettyString() {
        return "[browser: $browser, android: $android, ios: $ios]"
    }

    static String toPrettyString(List<Capabilities> capabilitiesList) {
        String prettyString = "["
        capabilitiesList.forEach() {
            prettyString += "${it.toPrettyString()}, "
        }
        prettyString = "${prettyString.take(prettyString.length()-2)}]"
        return prettyString
    }

    /**
     * Gets capability identifier from LazyMap
     */
    private static String getCapabilityIdentifier(List<LazyMap> capability) {
        if (capability) {
            StringBuilder builder = new StringBuilder()
            for (LazyMap map : capability)
                builder.append(map.containsKey(CAPABILITY_ID) ? "_" + map.get(CAPABILITY_ID) : "_" + map.hashCode())
            return builder.replaceFirst("_", "")
        } else {
            return ""
        }
    }
}
