import me.whereareiam.socialismus.gradle.plugin.JavaVersionPlugin
import me.whereareiam.socialismus.gradle.plugin.ModuleInfoPlugin
import me.whereareiam.socialismus.gradle.plugin.PublishPlugin

plugins {
    id("java")
    id("maven-publish")
}

pluginManager.apply(JavaVersionPlugin::class)
pluginManager.apply(ModuleInfoPlugin::class)
pluginManager.apply(PublishPlugin::class)

dependencies {
    // lombok
    annotationProcessor(rootProject.libs.lombok)
    compileOnly(rootProject.libs.lombok)

    // general
    compileOnly(rootProject.libs.annotations)
    compileOnly(rootProject.libs.bundles.adventure)
    compileOnly(rootProject.libs.commandant)
    compileOnly(rootProject.libs.configura)
    compileOnly(rootProject.libs.guice)
    compileOnly(rootProject.libs.keystone)
    implementation(rootProject.libs.attache.common)

    // test
    testImplementation(rootProject.libs.bundles.testing)
    testImplementation(rootProject.libs.commandant)
    testImplementation(rootProject.libs.configura)
    testImplementation(rootProject.libs.guice)
    testImplementation(rootProject.libs.keystone)
    testRuntimeOnly(rootProject.libs.junit.platform)
}