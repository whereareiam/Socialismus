plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://registry.whereareiam.me/maven/packages")
}

dependencies {
    implementation(libs.shadow.gradle.plugin)
    implementation(libs.toolkit.publish.maven)
}
