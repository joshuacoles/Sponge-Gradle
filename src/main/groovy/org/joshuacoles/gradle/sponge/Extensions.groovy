package org.joshuacoles.gradle.sponge

import groovy.transform.PackageScope
import groovy.transform.TupleConstructor

class Extensions {
    static class Plugin {
        String id, name
        String lang = 'java'
        Map<String, ?> dependency
    }

    static class Sponge {
        Object platforms = SpongePlatform.Type.FORGE
        Object version = '2.0'
    }

    @TupleConstructor
    static class SpongePlatform {
        Type type
        String version = 'LATEST'

        String getGroupId() { type.groupId }

        String getArtifactId() { type.artifactId }

        String getVersion() { this.version }

        SpongePlatform(Type type) {
            this.type = type
        }

        static enum Type {
            FORGE(group: 'org.spongepowered', artifact: 'sponge')

            private Type(Map<String, String> map) {
                this.groupId = map.group
                this.groupId = map.artifact
            }

            private String groupId, artifactId

            @PackageScope
            String getGroupId() { groupId }

            String getArtifactId() { artifactId }
        }

        String getComposed() {
            "$groupId:$artifactId:$version"
        }
    }
}
