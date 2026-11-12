import me.whereareiam.socialismus.buildlogic.packaging.CheckModuleExports
import me.whereareiam.socialismus.buildlogic.packaging.SocialismusModuleExtension
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.jvm.tasks.Jar

plugins {
    id("packaging-shaded-jar")
}

// The Socialismus versions a module of this build accepts: the one it is built with and every later one before the
// next major version, as Chassis compares the module's `host` range with Socialismus's host version.
val hostRange = "^" + rootProject.extra["socialismusHostVersion"]
val moduleVersion = rootProject.extra["socialismusModuleVersion"].toString()

val socialismusModule = extensions.create<SocialismusModuleExtension>("socialismusModule")

dependencies {
    add("compileOnly", project(":socialismus-module:module-api"))
}

val mainClasses = extensions.getByType<SourceSetContainer>()["main"].output.classesDirs

val checkModuleExports = tasks.register<CheckModuleExports>("checkModuleExports") {
    exports.set(socialismusModule.exports)
    classpath.from(
        mainClasses,
        configurations.named("compileClasspath"),
        configurations.named("runtimeClasspath")
    )
    report.set(layout.buildDirectory.file("module-exports/exports.txt"))
}

tasks.named<Jar>("shadowJar").configure {
    dependsOn(checkModuleExports)
    archiveBaseName.set(socialismusModule.name)
    archiveClassifier.set("")
}

tasks.named<Copy>("processResources").configure {
    val moduleExports = socialismusModule.exports
    inputs.property("moduleName", socialismusModule.name)
    inputs.property("moduleExports", moduleExports)
    inputs.property("moduleVersion", moduleVersion)
    inputs.property("hostRange", hostRange)

    filesMatching("module.yml") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "projectName" to socialismusModule.name.get(),
                "projectVersion" to moduleVersion,
                "host" to hostRange
            )
        )
    }

    // The exports come from socialismusModule { exports }, checked by checkModuleExports, never from the file itself.
    doLast {
        val descriptor = destinationDir.resolve("module.yml")
        if (!descriptor.isFile) return@doLast

        val text = descriptor.readText()
        if (Regex("(?m)^exports:").containsMatchIn(text))
            throw GradleException("module.yml lists exports; declare them in socialismusModule { exports } instead")

        val exports = moduleExports.get()
        if (exports.isEmpty()) return@doLast

        descriptor.writeText(text.trimEnd() + "\nexports: [" + exports.joinToString(", ") + "]\n")
    }
}
