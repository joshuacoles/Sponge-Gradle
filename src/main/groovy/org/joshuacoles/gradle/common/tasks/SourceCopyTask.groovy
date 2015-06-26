package org.joshuacoles.gradle.common.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet

import java.util.regex.Pattern

class SourceCopyTask extends DefaultTask {
    @InputFiles
    SourceDirectorySet source;

    @Input
    HashMap<String, Object> replacements = new HashMap<String, Object>();

    @Input
    ArrayList<String> includes = new ArrayList<String>();

    @OutputDirectory
    File output;

    @TaskAction
    public void doTask() throws IOException {
        PatternSet patterns = new PatternSet(includes: source.includes, excludes: source.excludes)

        Map<String, String> repl = (replacements.findAll { it.key != null && it.value != null })
                .map({ k, _ -> Pattern.quote(k) }, { _, v -> v.toString() })

        // start traversing tree
        source.srcDirTrees.findAll { it.dir.exists() && it.dir.directory }
                .map({ it.dir }, { project.fileTree(it).matching(source.filter).matching(patterns) })
                .each
                { entry ->
                    entry.value.visit { FileVisitDetails details ->
                        File file = details.file

                        getDest(file, entry.key, this.output).with {
                            parentFile.mkdirs()
                            createNewFile()
                        }

                        if (isIncluded(file)) repl.each { r -> file.text.replaceAll(r.key, r.value) }
                    }
                }
    }

    private traverseCallable(o) { if (o.respondsTo('call')) traverseCallable(o()) }

    private static File getDest(File input, File base, File baseOut) throws IOException {
        new File(baseOut, input.canonicalPath.remove(base.canonicalPath))
    }

    private boolean isIncluded(File file) throws IOException {
        includes.empty || includes.any { include ->
            file.canonicalPath.replace('\\', '/').endsWith(include.replace('\\', '/'))
        }
    }

    public void replace(String key, Object val) { replacements.put(key, val); }

    public void replace(Map<String, Object> map) { replacements.putAll(map); }

    public HashMap<String, Object> getReplacements() { return replacements; }

    public void include(String str) { includes.add(str); }

    public void include(List<String> strs) { includes.addAll(strs); }
}
