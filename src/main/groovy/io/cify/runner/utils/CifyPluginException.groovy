package io.cify.runner.utils

/**
 * Exception thrown in plugin
 *
 * Created by FOB Solutions
 */

class CifyPluginException extends Exception {

    /**
     * Cify plugin exception with message
     *
     * @param message message to send
     * */
    public CifyPluginException(String message) {
        super(message)
    }

    /**
     * Cify plugin exception with message and throwable
     *
     * @message message to send
     * @throwable another exception to pass
     * */
    public CifyPluginException(String message, Throwable throwable) {
        super(message, throwable)
    }


}
