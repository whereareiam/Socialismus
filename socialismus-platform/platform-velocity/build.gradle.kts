import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("socialismus.platform-runtime")
}

dependencies {
    implementation(projects.integrationPapiproxybridge)

    compileOnly(libs.bundles.velocity)
    compileOnly(libs.cloud.velocity)
    annotationProcessor(libs.velocity)
    implementation(libs.attache.velocity)
    implementation(libs.bundles.bStats.velocity)
}

tasks.withType<ShadowJar>().configureEach {
    archiveClassifier.set("VELOCITY")

    manifest {
        attributes["Plugin-Type"] = "VELOCITY"
    }
}
