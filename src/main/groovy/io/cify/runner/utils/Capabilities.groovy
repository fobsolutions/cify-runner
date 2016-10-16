package io.cify.runner.utils

import groovy.json.internal.LazyMap

/**
 * Created by FOB Solutions
 *
 * Set of capabilities
 */
class Capabilities {

    private static final CAPABILITY_ID = "capabilityId"

    LazyMap ios
    LazyMap android
    LazyMap browser

    /**
     * Add capability to every object
     * */
    public addCapabilitiesToAll(LazyMap extraCapabilities) {
        if (!browser.isEmpty()) {
            browser.putAll(extraCapabilities)
        }
        if (!android.isEmpty()) {
            android.putAll(extraCapabilities)
        }
        if (!ios.isEmpty()) {
            ios.putAll(extraCapabilities)
        }
    }

    /**
     * Generate unique string from capabilities object
     * */
    @Override
    public String toString() {
        getCapabilityIdentifier(getBrowser()) + "_" + getCapabilityIdentifier(getAndroid()) + "_" + getCapabilityIdentifier(getIos())
    }

    /**
     * Gets capability identifier from LazyMap
     * */
    private static String getCapabilityIdentifier(LazyMap capability) {
        capability.containsKey(CAPABILITY_ID) ? capability.get(CAPABILITY_ID) : capability.values().toString()
    }
}
