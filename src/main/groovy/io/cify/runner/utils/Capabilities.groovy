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
    List<LazyMap> custom = []

    void addCapabilitiesToAll(LazyMap extraCapabilities) {
        if (!browser.isEmpty()) {
            browser.each {
                it.putAll(extraCapabilities)
            }
        }

        if (!android.isEmpty()) {
            android.each {
                it.putAll(extraCapabilities)
            }
        }

        if (!ios.isEmpty()) {
            ios.each {
                it.putAll(extraCapabilities)
            }
        }

        if (!custom.isEmpty()) {
            custom.each {
                it.putAll(extraCapabilities)
            }
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
        String customCapsId = getCapabilityIdentifier(getCustom())

        browserCapsId = browserCapsId ? "_" + browserCapsId : ""
        androidCapsId = androidCapsId ? "_" + androidCapsId : ""
        iosCapsId = iosCapsId ? "_" + iosCapsId : ""
        customCapsId = customCapsId ? "_" + customCapsId : ""

        return browserCapsId + androidCapsId + iosCapsId + customCapsId
    }

    /**
     * Gets capability identifier from LazyMap list
     * */
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
