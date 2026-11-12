package me.whereareiam.socialismus.buildlogic.packaging

import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import xyz.wagyourtail.jvmdg.ClassDowngrader
import xyz.wagyourtail.jvmdg.cli.Flags
import xyz.wagyourtail.jvmdg.compile.ApiShader
import xyz.wagyourtail.jvmdg.compile.ZipDowngrader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Rewrites the jar an archive task wrote for an older Java with JvmDowngrader, and copies the stand-ins of the newer
 * Java APIs it calls into the jar under [shadePrefix], so it needs nothing beside itself. Entries under the
 * [untouched] paths stay as they are.
 */
class JarDowngrade(
        private val java: Provider<Int>,
        private val untouched: Provider<List<String>>,
        private val javaApi: FileCollection,
        private val classpath: FileCollection,
        private val shadePrefix: String
) : Action<Task> {
    override fun execute(task: Task) {
        val jar = (task as AbstractArchiveTask).archiveFile.get().asFile.toPath()
        val kept = untouched.get()
        val rest = jar.resolveSibling(jar.fileName.toString() + ".rest")
        val downgraded = jar.resolveSibling(jar.fileName.toString() + ".downgraded")
        val shaded = jar.resolveSibling(jar.fileName.toString() + ".shaded")
        val flags = Flags().apply {
            classVersion = java.get() + 44
            api = javaApi.files.toList()
            quiet = true
        }

        copy(listOf(jar), rest) { name -> kept.none(name::startsWith) }
        ClassDowngrader.downgradeTo(flags).use { downgrader ->
            ZipDowngrader.downgradeZip(downgrader, rest, classpath.files.map { it.toURI().toURL() }.toSet(), downgraded)
        }
        ApiShader.shadeApis(flags, shadePrefix, downgraded.toFile(), shaded.toFile(), javaApi.files)

        val result = jar.resolveSibling(jar.fileName.toString() + ".result")
        copy(listOf(shaded), result) { true }
        appendUntouched(jar, result, kept)
        Files.move(result, jar, StandardCopyOption.REPLACE_EXISTING)
        listOf(rest, downgraded, shaded).forEach(Files::deleteIfExists)
    }

    private fun copy(sources: List<Path>, target: Path, include: (String) -> Boolean) {
        ZipOutputStream(Files.newOutputStream(target)).use { out ->
            sources.forEach { source -> copyEntries(source, out, include) }
        }
    }

    private fun appendUntouched(original: Path, target: Path, kept: List<String>) {
        if (kept.isEmpty()) return

        val merged = target.resolveSibling(target.fileName.toString() + ".merged")
        ZipOutputStream(Files.newOutputStream(merged)).use { out ->
            copyEntries(target, out) { true }
            copyEntries(original, out) { name -> kept.any(name::startsWith) }
        }
        Files.move(merged, target, StandardCopyOption.REPLACE_EXISTING)
    }

    private fun copyEntries(source: Path, out: ZipOutputStream, include: (String) -> Boolean) {
        ZipInputStream(Files.newInputStream(source)).use { input ->
            generateSequence { input.nextEntry }.filter { include(it.name) }.forEach { entry ->
                out.putNextEntry(ZipEntry(entry.name).apply { time = entry.time })
                input.copyTo(out)
                out.closeEntry()
            }
        }
    }
}
