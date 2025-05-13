import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

tasks.withType<ShadowJar> {
    archiveClassifier.set("VELOCITY")
}

dependencies {
    "implementation"(project(":socialismus-integration:integration-papiproxybridge"))
    "implementation"(project(":socialismus-integration:integration-valiobungee"))

    "compileOnly"(libs.bundles.velocity)
    "compileOnly"(libs.cloud.velocity)
    "annotationProcessor"(libs.velocity)
    "implementation"(rootProject.libs.libby.velocity)
    "implementation"(rootProject.libs.bundles.bStats.velocity)
}