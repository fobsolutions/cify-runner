package io.cify.runner

/**
 * Parameters used in the Cify plugin.
 *
 * Created by FOB Solutions
 */
class CifyPluginExtension {

    /**
     *  Directories to use as source for step definitions. Defaults to src/test/java
     */
    String gluePackages = ""

    /**
     * Directories to look for feature files. Defaults to [src/test/resources]
     */
    String featureDirs = "src/test/resources"

    /**
     * Tags used to filter which scenarios should be run. Defaults to ~@Broken,~@Skip,~@Unstable,~@Todo
     */
    String tags = "~@Broken,~@Skip,~@Unstable,~@Todo"

    /**
     * Output formats for cucumber test results. Defaults to 'pretty,json:build/cify/reports/json/'
     */
    String cucumberPlugins = "pretty"

    /**
     * Execute a test dry run without actually executing tests. Defaults to false
     */
    String dryRun = "false"

    /**
     * Strict mode, fail if there are pending or skipped tests. Defaults to false
     */
    String strict = "false"

    /**
     * Format output in single color.  Defaults to false
     */
    String monochrome = "false"

    /**
     * Whether to cause a build failure on any test failures. Defaults to false
     */
    String ignoreFailures = "false"

    /**
     * How many tests in parallel. Defaults to 1
     * */
    String threads = "1"

    /**
     * Capabilities file path. Defaults to capabilities.json
     * */
    String capabilitiesFilePath = "capabilities.json"

    /**
     * Remote URL for device farms, if set then used in RemoteWebDriver. Defaults to empty string
     * */
    String farmUrl = ""

    /**
     * Device capabilities for every capability
     * */
    String extraCapabilities = ""

    /**
     * Capabilities from command line
     * */
    String capabilities = ""

    /**
     * Enable video recording from command line. Defaults to false
     * */
    String videoRecord = "false"

    /**
     * Directory where videos are saved. Defaults to build/cify/videos/
     * */
    String videoDir = "build/cify/videos/"

    /**
     * Generated capabilities list
     * */
    List capabilitiesSet = []

    /**
     * Feature files paths with given parameters
     * */
    List features = []

    /**
     * Help text to show user what options we have via command line
     * */
    String helpText =
            '''
    Options:

    Here you can find all the parameters we use inside our framework.
    You can change every parameter by sending it via command line or adding it into build script.

        Cify parameters:

            -Pthreads               Specify number of parallel threads. Default 1
                                    Usage:  ./gradlew cucumber -Pthreads=3

            -Penv                   Environment name to use.
                                    You can define all the parameters in envProperties file.
                                    Properties files should be located in root location, right next to build.gradle.
                                    If user passes env variable with value dev,
                                    then system will search for env-dev.envProperties file from root folder.
                                    Usage:  ./gradlew cucumber -Penv=dev

            -PcapabilitiesFilePath  Devices JSON file path. Defaults to capabilities.json
                                    Usage: ./gradlew cucumber -PcapabilitiesFilePath=capabilities.json

            -PfarmUrl               Remote URL for device farms, if set then used in RemoteWebDriver. Defaults to empty string
                                    Usage: ./gradlew cucumber -PfarmUrl=http://localhost:63342/

            -PextraCapabilities     Map of capabilities to add to every capability in list
                                    Usage: ./gradlew cucumber -PextraCapabilities=remote=http://localhost:63342/&secondParam=123

            -PvideoRecord           Record video for every device from creating driver til closing driver. Defaults to false
                                    Usage: ./gradlew cucumber -PvideoRecord=true

            -PvideoDir              Directory where videos are saved. Defaults to build/cify/videos
                                    Usage: ./gradlew cucumber -PvideoDir=project/videos/

        Cucumber parameters:

            -PgluePackages      Set a package to search step definitions in
                                Usage:  ./gradlew cucumber -PgluePackages=com/example/stepdefinitions,com/example2/stepdefinitions

            -PfeatureDirs       Set a package to search feature files,
                                Usage:  ./gradlew cucumber

            -Ptags              Run features/scenarios with certain tag only
                                Usage:  ./gradlew cucumber -Ptags=@smoke
                                        ./gradlew cucumber -Ptags=@android,@ios

            -PcucumberPlugins   Register a cucumber plugins
                                Usage:  ./gradlew cucumber -PcucumberPlugins=screenshot
                                        ./gradlew cucumber -PcucumberPlugins=screenshot,saucelabs

            -PdryRun            Execute a test dry run without actually executing tests. Defaults to false
                                Usage:  ./gradlew cucumber -PdryRun=false

            -Pstrict            Strict mode, fail if there are pending or skipped tests. Defaults to false
                                Usage:  ./gradlew cucumber -Pstrict=false

            -Pmonochrome        Format output in single color.  Defaults to false
                                Usage:  ./gradlew cucumber -Pmonochrome=false

            -PignoreFailures    Whether to cause a build failure on any test failures.
                                Usage:  ./gradlew cucumber -PignoreFailures=false

    Device usage:

    Here you can find information about running tests with and without capabilities file.

        Usable capabilities:
                            chrome
                            safari
                            opera
                            firefox
                            android
                            internetexplorer
                            ipad
                            iphone
                            phantomjs
                            edge

        With capabilities file:
                            Every capability parameter will be added to DesiredCapability object.
                            Every capability will be executed with all the tests provided.

                            Every capability MUST HAVE two parameters:
                            1) capability - system will use this to generate default desired capability for capability.
                                            See available capabilities on "Usable capabilities" section.
                            2) capabilityId - system will use this to execute tests against capabilities

                            User can add as many additional parameters as needed (like in 3. capability object)
                            and they will be added to DesiredCapability object.

                            Valid capabilities file structure:
                            {
                              "capabilities": [
                                {
                                  "capability": "chrome",
                                  "capabilityId": "localChrome"
                                },
                                {
                                  "capability": "firefox",
                                  "capabilityId": "localFirefox"
                                },
                                {
                                  "capability": "safari",
                                  "capabilityId": "safari",
                                  "custom": "customValue",
                                  "customParam2": "Cify"
                                }
                              ]
                            }

                            There is a possibility to create web drivers with only certain capabilities from list
                            by selecting capabilities with capabilityId.

                            Example:
                            ./gradlew cucumber -Pcapabilities=localChrome,localFirefox

        Without file:
                            There is a possibility to run tests without capabilities list.
                            For that user can add -Pcapabilities parameter to task with capability.
                            See available capabilities on "Usable capabilities" section.
                            Every capability will be used to create web driver with all the tests provided.

                            Example:
                            ./gradlew cucumber -Pcapabilities=chrome,firefox

                            If user don't provide capabilities json and capabilities parameter then tests are triggered,
                            but user can't call device with DeviceManager.getDevice(), correct way to call driver
                            is to call DeviceManager.getDevice("chrome") or any other capability from "Usable capabilities"
                            section.

        '''
}
