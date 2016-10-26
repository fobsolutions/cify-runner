package io.cify.runner.utils

/**
 * This class is used to build cucumber arguments List.
 *
 * Created by FOB Solutions
 */
class CucumberArgsBuilder {

    static final String TAGS_OPTION = '--tags'
    static final String PLUGIN_OPTION = '--plugin'
    static final String DRYRUN_OPTION = '--dry-run'
    static final String MONOCHROME_OPTION = '--monochrome'
    static final String STRICT_OPTION = '--strict'
    static final String GLUE_OPTION = '--glue'
    List tags = []
    List plugins = []
    List glue = []
    boolean dryRun
    boolean strict
    boolean monochrome
    String featureDir
    String taskName

    CucumberArgsBuilder(String taskName) {
        this.taskName = taskName
    }

    /**
     * Adds tags arguments. Currently only AND is supported e.g. Scenarios with @important AND @billing
     * @param tags Cucumber tags
     * @return
     */
    CucumberArgsBuilder addTags(String tags) {
        if (tags) {
            List tagsList = tags.tokenize(',')
            tagsList.each {
                this.tags << TAGS_OPTION
                this.tags << it
            }
        }

        return this
    }

    /**
     * Adds plugins to the list.
     * @param plugins
     * @return
     */
    CucumberArgsBuilder addPlugins(String plugins) {
        if (plugins) {
            List pluginsList = plugins.tokenize(',')
            pluginsList.each {
                this.plugins << PLUGIN_OPTION
                if (it.toString().startsWith("json:")) {
                    it = it + taskName + ".json"
                }
                this.plugins << it
            }
        }

        return this
    }

    /**
     * Adds glue files locations.
     * @param glue
     * @return
     */
    CucumberArgsBuilder addGlue(String glue) {
        if (glue) {
            List glueList = glue.tokenize(',')
            glueList.each {
                this.glue << GLUE_OPTION
                this.glue << it
            }
        }

        return this
    }

    /**
     * Set dry run parameter
     *
     * @return cucumber args builder class
     * */
    CucumberArgsBuilder setDryRun(String dryrun) {
        this.dryRun = dryrun.toBoolean()
        return this
    }

    /**
     * Set strict parameter
     *
     * @return cucumber args builder class
     * */
    CucumberArgsBuilder setStrict(String strict) {
        this.strict = strict.toBoolean()
        return this
    }

    /**
     * Set monochrome parameter
     *
     * @return cucumber args builder class
     * */
    CucumberArgsBuilder setMonochrome(String monochrome) {
        this.monochrome = monochrome.toBoolean()
        return this
    }

    /**
     * Set feature dir parameter
     *
     * @return cucumber args builder class
     * */
    CucumberArgsBuilder addFeatureDir(String featureDir) {
        if (featureDir) {
            this.featureDir = featureDir
        }
        return this
    }

    /**
     * Builds list from parameters
     *
     * @return list of arguments
     * */
    List build() {
        List args = []

        if (featureDir) {
            args << featureDir
        }

        if (tags) {
            args << tags
        }

        if (plugins) {
            args << plugins
        }

        if (glue) {
            args << glue
        }

        if (dryRun) {
            args << DRYRUN_OPTION
        }

        if (monochrome) {
            args << MONOCHROME_OPTION
        }

        if (strict) {
            args << STRICT_OPTION
        }

        return args?.flatten()
    }
}
