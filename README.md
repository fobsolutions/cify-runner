
1. <a href="#what">What Is Cify Runner?</a>
1. <a href="#usage">How To Use Cify Runner</a>
1. <a href="#parameters">Available parameters in Cify Runner</a>
1. <a href="#jenkins">How to configure Cify Runner in Jenkins</a>

<a name="what" />
## What Is Cify Runner?

Cify Runner is part of a open source test automation tool called Cify. Runner is responsible for parameters management, test configuration and test execution.

<a name="usage" />
## How To Use Cify Runner

### Installation
Add Gradle dependency to your project as following:

``` 
buildscript {
    repositories {
        mavenCentral()
        maven {
            url = 'https://cify-artifacts.s3.eu-central-1.amazonaws.com'
        }
    }
    dependencies {
        // Check the latest version above
        classpath('io.cify:cify-runner:1.4.2')
    }
}

apply plugin: 'cify-runner'
```

### Gradle tasks

Cify runner contains 4 tasks:

- cucumber
- help
- parameters
- cloneDeviceFarm

**Cucumber** task collects parameters, device capabilities and feature files, generates task for each feature file, and triggers tests.

**Help** tasks prints all runner parameters and helping information to console.

**Setup** parameters task collects all the information from properties file, command line, defaults, devices and holds them as plugin extension

**cloneDeviceFarm** Clones device farm into devicefarm folder from GitHub

### Capabilities usage
  
   Here you can find information about running tests with and without capabilities file.

##### Usable capabilities:

* chrome
* safari
* opera
* firefox
* android
* internetexplorer
* ipad
* iphone
* phantomjs
* edge

Capabilities file is in JSON format and defines capabilities for suite. Users can pass parameters to devices with capabilities json file. 

File contains:
- **capabilities**
- **strategy**

**capabilities**

Capabilities is a list of capabilities to test against. User can define as much capabilities for each device category as needed. If strategy is not defined, runner will create variations so that capabilities from different categories are tested with each other.

**strategy**

Optionally, you can specify String **"strategy"** to define capabilities parsing strategy. Default strategy is **"variations"**.
Possible values are defined in [CapabilitiesParser](https://github.com/fobsolutions/cify-runner/blob/master/src/main/groovy/io/cify/runner/utils/CapabilityParserNew.groovy) and include:
- **"all_in_one"** - capabilities parsing strategy, to put all capabilities into one [Capabilities](https://github.com/fobsolutions/cify-runner/blob/master/src/main/groovy/io/cify/runner/utils/Capabilities.groovy) object
- **"one_by_one"** - capabilities parsing strategy, to put each capability into a separate [Capabilities](https://github.com/fobsolutions/cify-runner/blob/master/src/main/groovy/io/cify/runner/utils/Capabilities.groovy) object
- **"variations"** - capabilities parsing strategy, that creates all possible variations of capabilities with one capability of each kind (browser, android, ios) per [Capabilities](https://github.com/fobsolutions/cify-runner/blob/master/src/main/groovy/io/cify/runner/utils/Capabilities.groovy) object

Valid capability file structure:
```
{
  "strategy": "variations",
  "capabilities": {
    "browser": [
      {
        "capability": "safari",
      },
      {
        "capability": "opera",
      },
      {
        "capability": "chrome",
        "version": "74"
      }
    ],
    "android": [
      {
        "capability": "android",
      }
    ],
    "ios": [
      {
        "capability": "ipad",
      }
    ]
  }
}
```

In this case there will be 3 different variations (tasks) to run:

1. Safari browser, Android device, iPad
1. Opera browser, Android device, iPad
1. Chrome browser , Android device, iPad

There is a possibility to pass capabilities from command line.

   Example:
  > ./gradlew cucumber -Pcapabilities='{"set": {"browser":[{"version":"52","capability":"chrome"}]}}'

----------

<a name="parameters" />
## Available parameters in Cify Runner

There are more than different configuration options in Cify Runner.

| Parameter | Description | Values |
| :-------: | :---------: | :-----:|
| threads |Specify number of parallel threads. Default 1 | integers |
| env | Environment name to use. Properties file name. Valid file name structure is env-demo.properties | demo |
| capabilitiesFilePath | Capabilities JSON file path. Defaults to capabilities.json | capabilities file name |
|capabilities |Desired capabilities|capabilities JSON file content|
|farmUrl| Remote URL to every capability (all drivers will be RemoteWebDrivers) | URL |
|gluePackages|Set a package to search step definitions from|Path to steps|
|featureDirs|Set a package to search feature files|Path to package|
|tags|Run features/scenarios with certain tag only|@-to include, ~@ to exclude|
|cucumberPlugins|Register a cucumber plugins|Path to formatter class|
|dryRun|Execute a test dry run without actually executing tests. Defaults to false|true,false|
|strict|Strict mode, fail if there are pending or skipped tests. Defaults to false|true,false|
|monochrome|Format output in single color. Defaults to false|true,false|
|ignoreFailures|Whether to cause a build failure on any test failures.|true,false|
|videoRecord|Whether to record all devices. Defaults to false|true,false|
|videoDir|Directory where to save videos. Defaults to build/cify/videos|relative path|
|credentials|Remote device farm providers credentials to be added to capabilities|{testdroid_apiKey=secret}|

----------

### System properties

Users can send system properties to framework and other processes like this:

    gradlew cucumber -Dcify.SOME_PROPERTY=randomValue

This is needed cause runner calls other system processes and they don't share same process nor thread.
    
<a name="jenkins" />
## How to configure Cify Runner in Jenkins

Cify Runner can be used in Jenkins as a default Gradle project.

### Preconditions
1. Jenkins with version 2.23+
1. Gradle plugin installed on Jenkins

### Step-By-Step guide

1. Create freestyle project
1. Check "Delete workspace before build starts" in Build Environment
1. Add build step "Invoke Gradle script"
1. Select use gradle wrapper
1. Insert "cucumber" with all the parameters you need into Tasks input

```
    Example: cucumber -Penv=demo -PfarmUrl=http://localhost:4444/wd/hub
```
### Cucumber HTML reports with Cify Runner

Users can use default Cucumber HTML report.

Cucumber reports plugin can be found in: <a href="https://github.com/jenkinsci/cucumber-reports-plugin">Cucumber reports plugin</a>

[![Analytics](https://ga-beacon.appspot.com/UA-109814182-1/cify-runner)](https://github.com/fobsolutions/cify-runner)
