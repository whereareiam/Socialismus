import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("whereami.convention.basic")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("BUKKIT")

    manifest {
        attributes(
            "Plugin-Type" to "BUKKIT"
        )
    }

    relocate("net.kyori.adventure", "me.whereareiam.socialismus.library.adventure")
}

dependencies {
    compileOnly(projects.socialismusApi)
    compileOnly(projects.socialismusCommon)
    compileOnly(projects.socialismusAdapterCommand)
    compileOnly(projects.socialismusAdapterModule)
    compileOnly(projects.socialismusPlatform.platformBukkit.common)
    compileOnly(libs.bundles.bukkit)
    compileOnly(libs.bundles.cloud)
    compileOnly(rootProject.libs.brigadier)
    compileOnly(rootProject.libs.cloud.paper)

    implementation(libs.attache.bukkit)
    implementation(projects.socialismusIntegration.integrationBstats)
    implementation(projects.socialismusIntegration.integrationPacketevents)
    implementation(projects.socialismusIntegration.integrationPlaceholderapi)
    implementation(projects.socialismusPlatform.platformBukkit.common)

    testImplementation(projects.socialismusApi)
    testImplementation(projects.socialismusCommon)
    testImplementation(projects.socialismusAdapterCommand)
    testImplementation(projects.socialismusAdapterModule)
    testImplementation(projects.socialismusPlatform.platformBukkit.common)
}

tasks.named<Copy>("processResources") {
    filter<ReplaceTokens>(
        "tokens" to mapOf(
            "projectName" to rootProject.name,
            "projectVersion" to project.version
        )
    )
}
