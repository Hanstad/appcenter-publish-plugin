# appcenter-publish-plugin

[![Kotlin 1.2.71](https://img.shields.io/badge/Kotlin-1.2.71-blue.svg)](http://kotlinlang.org)
[![Releases](https://img.shields.io/github/release/TeamWanari/appcenter-publish-plugin.svg)](https://github.com/TeamWanari/appcenter-publish-plugin/releases)
[![Gradle Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/gradle/plugin/appcenter-publish-plugin/plugin/maven-metadata.xml.svg?label=Gradle%20Plugin&style=flat)](https://plugins.gradle.org/plugin/com.ins.gradle.plugin.android.appcenter-publish-plugin)
[![MIT License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/jdarosTD/appcenter-publish-plugin/blob/master/LICENSE)

# Setup : Add the plugin to your project 

# Configuration




And the configuration block following the template :
```kotlin

appCenter {

    defaultConfig {
        apiToken = "default-api-token"               // Required. Token generated on your AppCenter account
        appOwner = "your-own-name"                  // Required. User or Organization name who owns the App on AppCenter
        destination = "user-group-name"             // Required. Name of the Test Group on AppCenter
        releaseNotes = "your-release-notes"        // Optional. Release notes...
        verbose = "false"                         // optional. Default is false
        appNameSuffix = "your-app-name-suffix"      // Optional 
    }
    productFlavors {
        flavor1 {
            appName = "flavor1-name"           // Custom name for flavor 1 app (optional)
            destination = "flavor1-user-group-name"     // Custom name for flavor 1 group of users (optional)
            releaseNotes = "flavor1-release-notes" // Optional. Release notes for flavor1
            verbose = true
        }

        flavor2 {
            appName = "flavor2-name"           // Custom name for flavor 2 app (optional)
            releaseNotes = "flavor2-release-notes" // Optional. Release notes for flavor2
             verbose = true
        }
        
        prod {
            appNameSuffix = "-Prod"
        }
        
         dev {
            appNameSuffix = "-Dev"
        }


    }

}
```

the app Variant configuration will be a merge of custom configuration and default.
For instance, for flavor2 configuration will be

```
        apiToken = "default-api-token"               
        appOwner = "your-own-name"                  
        destination = "user-group-name"             
        releaseNotes = "your-release-notes"       
        verbose = "true"                     
        appName = "flavor2-name"    
        releaseNotes = "flavor2-release-notes" 

```


This plugin is also compatible with multi-dimensions

for instance you can define in android DSL : 

```kotlin
 flavorDimensions "dimensionA", "dimensionB", "dimensionC"

    productFlavors {

        company        {  dimension 'dimensionA' }

        flavor1 {
            dimension 'dimensionB'
            ...
          
        }
        flavor2 {
            dimension 'dimensionB'
            ....
        }

        prod  { dimension 'dimensionC' 
                ...
        }
        dev { dimension 'dimensionC'
                ...
        }

    }

```

in this situation, variant app "companyFlavor2DevRelease" configuration will be a merge of each flavors with respect of dimension order in the list: 

```
        apiToken = "default-api-token"               
        appOwner = "your-own-name"                  
        destination = "user-group-name"             
        releaseNotes = "your-release-notes"       
        verbose = "true"                     
        appName = "flavor2-name"    
        releaseNotes = "flavor2-release-notes" 
        appNameSuffix = "-Dev"

```

# Usage

The appcenter-publish-plugin will add tasks for each variant following the pattern : 

```
  upload${variantName}AppCenter
```

for instance in our last example you will be able to call the task this way: 

```
  ./gradlew app:uploadCompanyFlavor2DevRelease
```

License
-------
```
MIT License

Copyright (c) 2018 TeamWanari

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
