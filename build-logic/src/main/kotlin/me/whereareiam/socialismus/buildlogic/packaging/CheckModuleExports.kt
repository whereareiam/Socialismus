package me.whereareiam.socialismus.buildlogic.packaging

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.zip.ZipFile

/**
 * Fails when a module exports a package that has no class in the module or on its classpath, so a renamed package or
 * a typo in `socialismusModule { exports }` breaks the build instead of a server.
 */
@CacheableTask
abstract class CheckModuleExports : DefaultTask() {
    @get:Input
    abstract val exports: ListProperty<String>

    @get:Classpath
    abstract val classpath: ConfigurableFileCollection

    @get:OutputFile
    abstract val report: RegularFileProperty

    @TaskAction
    fun check() {
        val invalid = exports.get().filter { !it.matches(PACKAGE_NAME) }
        if (invalid.isNotEmpty())
            throw GradleException("Exports must be package names, such as org.jdbi: ${invalid.joinToString()}")

        val missing = exports.get().filter { exported -> classpath.files.none { hasClassesOf(it, exported) } }
        if (missing.isNotEmpty())
            throw GradleException("Exported packages have no class in the module or on its classpath: ${missing.joinToString()}")

        report.get().asFile.writeText(exports.get().joinToString("\n"))
    }

    private fun hasClassesOf(location: File, exported: String): Boolean {
        val path = exported.replace('.', '/') + "/"
        if (location.isDirectory) return location.resolve(path).walk().any { it.isFile && it.name.endsWith(".class") }
        if (!location.isFile) return false

        return ZipFile(location).use { jar ->
            jar.stream().anyMatch { it.name.startsWith(path) && it.name.endsWith(".class") }
        }
    }

    private companion object {
        val PACKAGE_NAME = Regex("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)*")
    }
}
