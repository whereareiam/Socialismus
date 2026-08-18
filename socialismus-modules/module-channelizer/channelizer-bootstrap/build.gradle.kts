import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.jvm.tasks.Jar

plugins {
    id("runtime")
}

dependencies {
    implementation(project(":module-channelizer-api"))
    implementation(project(":module-channelizer-common"))
    implementation(project(":module-channelizer-platform-bukkit"))
    compileOnly(project(":socialismus-module:module-api"))
}

tasks.named<Jar>("shadowJar").configure {
    archiveBaseName.set("SocialismusChannelizer")
    archiveClassifier.set("")
}

tasks.named<Copy>("processResources").configure {
    filesMatching("module.json") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "projectName" to "SocialismusChannelizer",
                "projectVersion" to project.version
            )
        )
    }
}
