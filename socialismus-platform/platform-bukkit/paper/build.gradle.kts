import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("platform")
    id("platform-descriptor")
    alias(libs.plugins.attache)
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_25.toString()
    targetCompatibility = JavaVersion.VERSION_25.toString()
}

dependencies {
    implementation(projects.platformBukkitCommon)
    implementation(projects.integrationPlaceholderapi)

    compileOnly(libs.bundles.paper)
    compileOnly(libs.cloud.paper)
    compileOnly(libs.brigadier)
    implementation(libs.attache.paper)

    attache(libs.cloud.paper)
    attache(libs.cloud.minecraft.extras)
}

tasks.withType<ShadowJar>().configureEach {
    archiveClassifier.set("PAPER")

    manifest {
        attributes["Plugin-Type"] = "PAPER"
    }
}
