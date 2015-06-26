package org.joshuacoles.gradle.common

import org.codehaus.groovy.reflection.CachedClass
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl
import org.gradle.api.Project
import org.gradle.api.Task

import static org.codehaus.groovy.runtime.m12n.ExtensionModuleScanner.MODULE_META_INF_FILE

/**
 * Created by joshuacoles on 20/06/2015.
 */
class PluginSupport {
    private final Project project

    PluginSupport(Project project) {
        this.project = project
    }

    File projectFile(String path) { project.file(path) }

    File projectFile(File path) { project.file(path.canonicalPath) }

    public <T extends Task> T makeTask(String name, Class<T> type) {
        (T) project.task(type: type, name)
    }

    public <T> T extension(Class<T> c) { project.extensions.getByType(c) as T }

    boolean hasPlugin(String string) {
        project.plugins.hasPlugin(string)
    }

    public <T extends Task> T findTask(String task, Class<T> clazz) {
        project.getTasksByName(task, false)[0] as T
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    void registerExtensionModule() {
        Map<CachedClass, List<MetaMethod>> map = [:]
        ClassLoader classLoader = Thread.currentThread().contextClassLoader

        assert classLoader != null

        classLoader.getResources(MODULE_META_INF_FILE).each { url ->
            assert url != null
            url.withInputStream { stream ->
                assert stream != null
                Properties properties = new Properties()
                properties.load(stream)
                assert !properties.entrySet().empty
                if (properties.getProperty('moduleName') == 'sponge-gradle') {
                    (GroovySystem.metaClassRegistry as MetaClassRegistryImpl)
                            .registerExtensionModuleFromProperties(properties, classLoader, map)
                }
            }
        }

        assert !map.isEmpty()
        println map

        map.each { cachedClass, methods ->
            cachedClass.addNewMopMethods(methods)
        }

        assert [:].respondsTo('map')
    }
}
