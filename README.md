gradle-bom-plugin
=================

Bill of Materials Plugin for Gradle to Manage Versions of 3rd Party Libraries in a Central Space

Usage:

    buildScript {
        apply plugin: 'bill-of-materials'
        apply file: "$rootDir/versions.gradle"
    }