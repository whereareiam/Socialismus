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
    maven("https://maven.whereareiam.me/release")
    maven("https://maven.whereareiam.me/development")
}

dependencies {
    implementation(libs.shadow.gradle.plugin)
    implementation(libs.toolkit.publish.maven)
}
