package io.cify.runner

/**
 * This class is responsible for holding constant values
 *
 * Created by FOB Solutions
 */

class Constants {

    /**
     * Prefix for system properties
     * Example: -Dcify.console=DEBUG
     * */
    public static String CIFY_SYSTEM_PROPERTY_PREFIX = "cify."

    /**
     * Capabilities accepted by selenium
     * */
    public static enum Capabilities {
        CHROME,
        SAFARI,
        OPERA,
        FIREFOX,
        ANDROID,
        INTERNETEXPLORER,
        IPAD,
        IPHONE,
        PHANTOMJS,
        EDGE
    }
}
