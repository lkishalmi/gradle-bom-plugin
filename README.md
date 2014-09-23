gradle-bom-plugin
=================

Bill of Materials Plugin for Gradle to Manage Versions of 3rd Party Libraries
in a Central Space

The idea of this plugin has came from experience converting large multi-module
Maven project to gradle.

1. While `gradle init` did it's job to convert dependencies, there were 
   differences in the final 3rd party library usage due to the master pom's
   `<dependencyManagement>` section, where the project set the version of their
   3rd party libraries.
2. For large project it is really useful to have a list of the referenced 
   libraries and their version.

Usage: `build.gradle`
```
buildscript {
    dependencies {
        classpath files('<path to the gradle-bom-plugin-0.1.0.jar')
    }
}

apply plugin: 'java'
apply plugin: 'war'
apply plugin: 'bill-of-materials'
apply file: "$rootDir/versions.gradle"

dependencies {
    compile BOM['org.springframework:spring-core']
    compile BOM['commons-logging:commons-logging']
    providedCompile BOM[['javax.servlet:servlet-api']
}
```
`versions.gradle`
```
BOM['org.springframework'] = '3.0.5.RELEASE'
BOM[':commons-logging'] = '1.1.1'
BOM[['javax.servlet:servlet-api'] = '2.5'
```