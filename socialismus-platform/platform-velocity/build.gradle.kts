import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("platform")
    alias(libs.plugins.attache)
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}

dependencies {
    compileOnly(project(":socialismus-module:module-api"))
    implementation(projects.integrationPapiproxybridge)

    compileOnly(libs.bundles.velocity)
    annotationProcessor(libs.velocity)
    implementation(libs.attache.velocity)
    implementation(libs.bundles.bStats.velocity)

    attache(libs.cloud.velocity)
}

tasks.withType<ShadowJar>().configureEach {
    archiveClassifier.set("VELOCITY")

    manifest {
        attributes["Plugin-Type"] = "VELOCITY"
    }
}
