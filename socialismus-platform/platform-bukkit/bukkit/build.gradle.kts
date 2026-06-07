import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("platform")
    id("platform-descriptor")
    alias(libs.plugins.attache)
}

dependencies {
    implementation(projects.platformBukkitCommon)
    implementation(projects.integrationPlaceholderapi)

    compileOnly(libs.bundles.bukkit)
    compileOnly(libs.cloud.paper)
    compileOnly(libs.brigadier)
    implementation(libs.attache.bukkit)

    attache(libs.adventure)
    attache(libs.adventure.minimessage)
    attache(libs.adventure.plain)
    attache(libs.adventure.gson)
    attache(libs.adventure.platform.bukkit)
    attache(libs.cloud.paper)
    attache(libs.cloud.minecraft.extras)
    attache(libs.brigadier)
}

tasks.withType<ShadowJar>().configureEach {
    archiveClassifier.set("BUKKIT")

    manifest {
        attributes["Plugin-Type"] = "BUKKIT"
    }

    relocate("net.kyori.adventure", "me.whereareiam.socialismus.library.adventure")
}
