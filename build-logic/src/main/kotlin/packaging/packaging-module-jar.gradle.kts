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

tasks.named<Jar>("shadowJar").configure {
    archiveBaseName.set(socialismusModule.name)
    archiveClassifier.set("")
}

tasks.named<Copy>("processResources").configure {
    inputs.property("moduleName", socialismusModule.name)
    inputs.property("projectVersion", project.version)
    inputs.property("socialismusVersionPattern", socialismusVersionPattern)

    filesMatching("module.json") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "projectName" to socialismusModule.name.get(),
                "projectVersion" to project.version,
                "dependency" to socialismusVersionPattern
            )
        )
    }
}
