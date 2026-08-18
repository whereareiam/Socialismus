import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.jvm.tasks.Jar

plugins {
    id("runtime")
}

dependencies {
    implementation(project(":module-bubbler-api"))
    implementation(project(":module-bubbler-command"))
    implementation(project(":module-bubbler-common"))
    compileOnly(project(":socialismus-module:module-api"))
    compileOnly(libs.packetevents)
}

tasks.named<Jar>("shadowJar").configure {
    archiveBaseName.set("Bubbler")
    archiveClassifier.set("")
}

tasks.named<Copy>("processResources").configure {
    filesMatching("module.json") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "projectName" to "Bubbler",
                "projectVersion" to project.version,
                "dependency" to rootProject.extra["socialismusModuleDependencyPattern"]
            )
        )
    }
}
