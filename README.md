gradle-bom-plugin
=================

Bill of Materials Plugin for Gradle to Manage Versions of 3rd Party Libraries
in a Central place.

There is a similar plugin [dependency-management-plugin from Spring][1]

The idea of this plugin has came from experience converting large multi-module
Maven projects to Gradle.

1. While `gradle init` does it's job to convert dependencies, there were 
   differences in the final 3rd party library usage due to the master pom's
   `<dependencyManagement>` section, where the project locks the used version of their
   3rd party libraries.
2. For large project it is really useful to have a list of the referenced 
   libraries and their version.

How Does it Work
----------------
This plugin adds a BOM extension to the project DSL. This BOM is just a little bit more than a hashmap which is used to store version numbers. It uses keys (rules) like: `[<group>][:][<module>]` where either thr group or module is optional.
Querying this BOM with `<group>:<module>` would result a full dependency spec. string as: `<group>:<module>:<version>` (or empty string at the moment if the requested module version is not in the BOM)
Querying is done in the following priority order:

1. query for `<group>:<module>`
2. query for `:<module>`
3. query for `<group>:`

Besides storing versions, this plugin extends dependency resolver to force 3rd party library versions listed in BOM in transitive dependencies. This behavior simulates the version locking functionality of the `<dependencyManagement>` section of Maven `pom.xml`-s.

Usage
-----
`build.gradle`:
```
buildscript {
    dependencies {
        classpath files('<path to the gradle-bom-plugin-0.1.0.jar>')
    }
}

apply plugin: 'java'
apply plugin: 'war'
apply plugin: 'com.github.lkishalmi.bill-of-materials'
apply file: "$rootDir/versions.gradle"

dependencies {
    compile 'org.springframework:spring-core'
    compile 'commons-logging:commons-logging'
    providedCompile group: 'javax.servlet', name: 'servlet-api'
}
```
`versions.gradle`:
```
BOM['org.springframework'] = '3.0.5.RELEASE'
BOM[':commons-logging'] = '1.1.1'
BOM['javax.servlet:servlet-api'] = '2.5'
```
or
```
BOM.rules([
    'org.springframework' : '3.0.5.RELEASE',
    ':commons-logging' : '1.1.1',
    'javax.servlet:servlet-api' : '2.5'
])
```

Usage in Multi-Project Builds
-----------------------------
`build.gradle`:
```
buildscript {
    dependencies {
        classpath files('<path to the gradle-bom-plugin-0.1.0.jar>')
    }
}

subprojects {
   ...
   apply plugin: 'java'
   apply plugin: 'bill-of-materials'
   apply file: "$rootDir/versions.gradle"
   
   ...
}
```

[1]: https://github.com/spring-gradle-plugins/dependency-management-plugin