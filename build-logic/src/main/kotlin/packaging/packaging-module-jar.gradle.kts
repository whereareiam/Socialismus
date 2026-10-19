import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import me.whereareiam.socialismus.buildlogic.packaging.CheckModuleExports
import me.whereareiam.socialismus.buildlogic.packaging.SocialismusModuleExtension
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.jvm.tasks.Jar

plugins {
    id("packaging-shaded-jar")
}

// The Socialismus versions a module accepts: any release, release candidate or development build.
val socialismusVersionPattern = "((?:[0-9]+\\\\.){2}[0-9]+(?:-[A-Za-z0-9._-]+)?|dev(?:-[A-Za-z0-9._-]+)?)"

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
    inputs.property("projectVersion", project.version)
    inputs.property("socialismusVersionPattern", socialismusVersionPattern)

    filesMatching("module.json") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "projectName" to socialismusModule.name.get(),
                "projectVersion" to project.version,
                "socialismus" to socialismusVersionPattern
            )
        )
    }

    // The exports come from socialismusModule { exports }, checked by checkModuleExports, never from the file itself.
    doLast {
        val moduleJson = destinationDir.resolve("module.json")
        if (!moduleJson.isFile) return@doLast

        @Suppress("UNCHECKED_CAST")
        val module = JsonSlurper().parse(moduleJson) as MutableMap<String, Any?>
        if (module.containsKey("exports"))
            throw GradleException("module.json lists exports; declare them in socialismusModule { exports } instead")

        val exports = moduleExports.get()
        if (exports.isEmpty()) return@doLast

        val withExports = LinkedHashMap<String, Any?>()
        module.forEach { (key, value) ->
            if (key == "main") withExports["exports"] = exports
            withExports[key] = value
        }
        withExports.putIfAbsent("exports", exports)
        moduleJson.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(withExports)) + "\n")
    }
}
