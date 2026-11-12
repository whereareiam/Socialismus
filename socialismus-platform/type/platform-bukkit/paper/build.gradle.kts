plugins {
    id("packaging-platform-jar")
    id("packaging-downgraded-jar")
    alias(libs.plugins.attache)
}

platform {
    name.set("PAPER")
    descriptors.add("paper-plugin.yml")
}

// The Paper API of 26.x is built for Java 25.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

downgrade {
    // Paper 1.20.6, the oldest version whose API the jar calls, runs on Java 21.
    java.set(21)
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
