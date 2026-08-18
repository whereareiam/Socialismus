import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.jvm.tasks.Jar

plugins {
    id("runtime")
}

dependencies {
    implementation(project(":module-essentials-api"))
    implementation(project(":module-essentials-command"))
    implementation(project(":module-essentials-common"))
    implementation(project(":module-essentials-feature"))
    compileOnly(project(":socialismus-module:module-api"))
}

tasks.named<Jar>("shadowJar").configure {
    archiveBaseName.set("Essentials")
    archiveClassifier.set("")
}

tasks.named<Copy>("processResources").configure {
    filesMatching("module.json") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "projectName" to "Essentials",
                "projectVersion" to project.version,
                "dependency" to rootProject.extra["socialismusModuleDependencyPattern"]
            )
        )
    }
}
