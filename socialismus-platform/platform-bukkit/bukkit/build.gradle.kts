import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("socialismus.platform-bukkit")
}

dependencies {
    compileOnly(libs.bundles.bukkit)
    implementation(libs.attache.bukkit)
}

tasks.withType<ShadowJar>().configureEach {
    archiveClassifier.set("BUKKIT")

    manifest {
        attributes["Plugin-Type"] = "BUKKIT"
    }

    relocate("net.kyori.adventure", "me.whereareiam.socialismus.library.adventure")
}
