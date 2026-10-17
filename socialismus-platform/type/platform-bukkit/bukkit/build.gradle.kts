import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("packaging-platform-jar")
    alias(libs.plugins.attache)
}

platform {
    name.set("BUKKIT")
    // Its shared classes use a relocated Adventure, so they differ from those of Paper and Velocity.
    bundled.set(false)
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
    relocate("net.kyori.adventure", "me.whereareiam.socialismus.library.adventure")
}
