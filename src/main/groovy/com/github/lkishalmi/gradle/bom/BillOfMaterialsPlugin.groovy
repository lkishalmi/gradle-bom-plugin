package com.github.lkishalmi.gradle.bom

import org.gradle.api.IllegalDependencyNotation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.logging.Logger

/**
 *
 * @author Laszlo Kishalmi
 */
class BillOfMaterialsPlugin  implements Plugin<Project> {

    public void apply(Project project) {
        VersionStore store = project.getExtensions().create("BOM", VersionStore.class, project.logger);
        
        project.configurations.all {
            resolutionStrategy.eachDependency {
                DependencyResolveDetails details ->
                def version = store.getVersion(details.requested.group, details.requested.name)
                if (version != null) {
                    details.useVersion(version)
                }
            }
        }
    }

    private static class VersionStore {

        final Logger logger;
        
        VersionStore(Logger logger) {
            this.logger = logger;
        }
        
        Map<String, String> rules = new HashMap<>();

        String getAt(String moduleSpec) {
            String version = rules.get(moduleSpec);
            if (version == null) {
                int sep = moduleSpec.indexOf(':');
                if (sep < 0) {
                    return "";
                } else {
                    return get(moduleSpec.substring(0, sep), moduleSpec.substring(sep + 1));
                }
            }
            return moduleSpec + ':' + version;
        }

        String get(String group, String module) {
            String version = getVersion(group, module);
            if (version == null) {
                throw new IllegalDependencyNotation("There is no rule in BOM which could define version for: '$group:$module'")
            }
            return group + ':' + module + ':' + version;
        }

        public String putAt(String moduleSpec, String version) {
            if (moduleSpec.indexOf(':') < 0) {
                moduleSpec = moduleSpec + ':';
            }
            return rules.put(moduleSpec, version);
        }
        
        public void rules(Map<String, String> map) {
            for ( e in map ) {
                putAt(e.key, e.value)
            }
        }

        public String getVersion(String group, String module) {
            String version = rules.get(group + ':' + module);
            if (version == null) {
                version = rules.get(':' + module);
                String gversion = rules.get(group + ':');
                if (version == null) {
                    version = gversion;
                } else {
                    if ((gversion != null) && !version.equals(gversion)) {
                        logger.warn("For $group:$module module rule dictates version: $version, while group rule dictates version: $gversion. \n\
                               Module rule is going to be used with version: $version")
                    }
                }
            }
            return version;
        }
    }
}

