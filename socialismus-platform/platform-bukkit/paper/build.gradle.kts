import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.kotlin.dsl.filter

plugins {
    id("whereami.convention.basic")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("PAPER")

    manifest {
        attributes(
            "Plugin-Type" to "PAPER"
        )
    }
}

dependencies {
    compileOnly(projects.socialismusCommon)
    compileOnly(projects.socialismusApi)
    compileOnly(projects.socialismusAdapterCommand)
    compileOnly(projects.socialismusAdapterModule)
    compileOnly(libs.bundles.cloud)
    compileOnly(libs.bundles.paper)
    compileOnly(rootProject.libs.brigadier)
    compileOnly(rootProject.libs.cloud.paper)

    implementation(libs.attache.paper)
    implementation(projects.socialismusIntegration.integrationBstats)
    implementation(projects.socialismusIntegration.integrationPacketevents)
    implementation(projects.socialismusIntegration.integrationPlaceholderapi)
    implementation(projects.socialismusPlatform.platformBukkit.common)
}

tasks.named<Copy>("processResources") {
    filter<ReplaceTokens>(
        "tokens" to mapOf(
            "projectName" to rootProject.name,
            "projectVersion" to project.version
        )
    )
}
