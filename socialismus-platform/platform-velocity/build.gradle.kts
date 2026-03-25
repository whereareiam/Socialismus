import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
    id("whereami.convention.basic")
    id("whereami.convention.shadow")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("VELOCITY")

    manifest {
        attributes(
            "Plugin-Type" to "VELOCITY"
        )
    }
}

dependencies {
    annotationProcessor(libs.velocity)

    compileOnly(projects.socialismusAdapterModule)
    compileOnly(projects.socialismusAdapterCommand)
    compileOnly(projects.socialismusApi)
    compileOnly(projects.socialismusCommon)
    compileOnly(libs.bundles.cloud)
    compileOnly(libs.bundles.velocity)
    compileOnly(libs.cloud.velocity)

    implementation(projects.socialismusIntegration.integrationBstats)
    implementation(projects.socialismusIntegration.integrationPacketevents)
    implementation(projects.socialismusIntegration.integrationPapiproxybridge)
    implementation(rootProject.libs.attache.velocity)
    implementation(rootProject.libs.bundles.bStats.velocity)

    testImplementation(projects.socialismusAdapterModule)
    testImplementation(projects.socialismusAdapterCommand)
    testImplementation(projects.socialismusApi)
    testImplementation(projects.socialismusCommon)
}
