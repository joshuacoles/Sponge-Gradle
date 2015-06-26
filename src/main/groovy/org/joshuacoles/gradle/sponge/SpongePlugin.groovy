package org.joshuacoles.gradle.sponge

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.ScalaSourceSet
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.AbstractCompile
import org.joshuacoles.common.Function
import org.joshuacoles.gradle.common.PluginSupport
import org.joshuacoles.gradle.common.tasks.SourceCopyTask

import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME

@SuppressWarnings("GroovyUnusedDeclaration")
class SpongePlugin implements Plugin<Project> {
    @Delegate
    PluginConstants constants

    @Delegate
    PluginSupport support

    /**
     * Apply this plugin to the given target object.
     *
     * @param project The target object
     */
    @Override
    void apply(Project project) {
        this.support = new PluginSupport(project)

        registerExtensionModule()

        this.constants = new PluginConstants(project)

        project.with {
            if (!convention.plugins.get("java")) throw new Exception('You must have some form of java plugin to use sponge gradle!!!!')

            repositories.maven {
                url 'http://repo.spongepowered.org/maven'
            }

            repositories.jcenter()

            apply plugin: 'idea'
            apply plugin: 'eclipse'

            extensions.create "sponge", Extensions.Sponge
            extensions.create "plugin", Extensions.Plugin

            dependencies.add 'compile',
                    "org.spongepowered:spongeapi:${extension(Extensions.Sponge).version}"

            tasks.addAll(replaceSourceTokens(project))

            task('spongeLicense') {
                project.file('LICENSE').with {
                    createNewFile()
                    text = 'https://raw.githubusercontent.com/SpongePowered/SpongeAPI/master/LICENSE.txt'.toURL().text
                }
            }
        }
    }

    private final SourceCopyTask[] replaceSourceTokens(Project project) {
        def suppliers = [doReplaceSourceToken(project, 'java', { it.java })]

        if (project.plugins.hasPlugin('groovy')) {
            suppliers << doReplaceSourceToken(project, 'groovy') {
                new DslObject(it).convention.plugins.groovy.asType(GroovySourceSet).groovy
            }
        }

        if (project.plugins.hasPlugin('scala')) {
            suppliers << doReplaceSourceToken(project, 'scala') {
                new DslObject(it).convention.plugins.scala.asType(ScalaSourceSet).scala
            }
        }

        return suppliers.collect { it(project) }
    }

    private Closure<SourceCopyTask> doReplaceSourceToken(Project project, String lang, Function<SourceSet, SourceDirectorySet> sourceSupplier) {
        return {
            SourceSet main = (project.convention.plugins.get("java") as JavaPluginConvention)
                    .sourceSets.getByName(MAIN_SOURCE_SET_NAME);

            File dir = projectFile "$TEMP_SOURCES_DIR/$lang"

            SourceCopyTask task =
                    makeTask "replaceSourceTokens${lang.capitalize()}",
                            SourceCopyTask

            task.source = sourceSupplier(main)
            task.output = dir

            AbstractCompile compile = findTask(main.getCompileTaskName(lang), AbstractCompile)
            compile.dependsOn "replaceSourceTokens${lang.capitalize()}"
            compile.source = dir

            task.replace(['@[plugin.version]': project.version,
                          '@[plugin.id]'     : extension(Extensions.Plugin).id,
                          '@[plugin.name]'   : extension(Extensions.Plugin).name])
            return task
        }
    }
}
