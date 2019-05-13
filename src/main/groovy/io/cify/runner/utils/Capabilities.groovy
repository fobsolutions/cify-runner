package io.cify.runner.utils

import groovy.json.internal.LazyMap

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
     * Generate unique string from capabilities object
     * */
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
