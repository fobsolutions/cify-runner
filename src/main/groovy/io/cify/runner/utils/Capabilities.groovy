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

    LazyMap getIos() {
        return ios
    }

    void setIos(ios) {
        this.ios = ios
    }

    LazyMap getAndroid() {
        return android
    }

    void setAndroid(android) {
        this.android = android
    }

    LazyMap getBrowser() {
        return browser
    }

    void setBrowser(browser) {
        this.browser = browser
    }

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
