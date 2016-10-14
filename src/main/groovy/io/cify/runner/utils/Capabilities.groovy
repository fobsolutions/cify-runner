package io.cify.runner.utils

import groovy.json.internal.LazyMap

/**
 * Created by FOB Solutions
 *
 * Set of capabilities
 */
class Capabilities {

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
}
