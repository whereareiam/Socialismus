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
    // adventure-platform-bukkit asks for Adventure 4; declaring these keeps its Adventure on the version above.
    attache(libs.adventure.legacy)
    attache(libs.adventure.json.legacy)
    attache(libs.adventure.nbt)
    attache(libs.cloud.paper)
    attache(libs.cloud.minecraft.extras)
}

// Relocated, so the Adventure the jar loads never meets the one some servers ship.
val adventureRelocations = mapOf(
    "net.kyori.adventure" to "me.whereareiam.socialismus.library.adventure",
    "net.kyori.option" to "me.whereareiam.socialismus.library.option"
)

attache {
    // Every library the jar downloads, also those other projects declare, such as Cloud, Commandant and Keystone.
    adventureRelocations.forEach { (pattern, relocatedPattern) -> relocate(pattern.replace(".", "{}"), relocatedPattern) }
}

tasks.withType<ShadowJar>().configureEach {
    adventureRelocations.forEach { (pattern, relocatedPattern) -> relocate(pattern, relocatedPattern) }
}
