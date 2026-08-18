import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.jvm.tasks.Jar

plugins {
    id("runtime")
}

dependencies {
    implementation(project(":module-chirper-api"))
    implementation(project(":module-chirper-command"))
    implementation(project(":module-chirper-common"))
    compileOnly(project(":socialismus-module:module-api"))
}

tasks.named<Jar>("shadowJar").configure {
    archiveBaseName.set("Chirper")
    archiveClassifier.set("")
}

tasks.named<Copy>("processResources").configure {
    filesMatching("module.json") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "projectName" to "Chirper",
                "projectVersion" to project.version,
                "dependency" to rootProject.extra["socialismusModuleDependencyPattern"]
            )
        )
    }
}
