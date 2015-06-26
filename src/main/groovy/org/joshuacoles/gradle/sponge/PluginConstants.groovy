package org.joshuacoles.gradle.sponge

import org.gradle.api.Project

@Newify(File)
class PluginConstants {
    private Project project

    final File BUILD_DIR
    final File TEMP_SOURCES_DIR

    PluginConstants(Project project) {
        this.project = project

        BUILD_DIR = File(project.buildDir, 'sponge')
        TEMP_SOURCES_DIR  = File(BUILD_DIR, 'sources')

        make()
    }

    void remake() {
        clean()
        make()
    }

    void clean() {
        this.metaClass.properties.each {
            if (it.type == File && it.name.contains('DIR')) project.delete(it.getProperty(this))
        }
    }

    void make() {
        this.metaClass.properties.each {
            if (it.type == File && it.name.contains('DIR')) project.mkdir(it.getProperty(this))
        }
    }
}
