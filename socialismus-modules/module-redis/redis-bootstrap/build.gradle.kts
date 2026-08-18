import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.jvm.tasks.Jar

plugins {
    id("runtime")
}

dependencies {
    implementation(project(":module-redis-api"))
    implementation(project(":module-redis-common"))
    compileOnly(project(":socialismus-module:module-api"))
}

tasks.named<Jar>("shadowJar").configure {
    archiveBaseName.set("SocialismusRedis")
    archiveClassifier.set("")
}

tasks.named<Copy>("processResources").configure {
    filesMatching("module.json") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "projectName" to "SocialismusRedis",
                "projectVersion" to project.version
            )
        )
    }
}
