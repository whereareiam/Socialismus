import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.compile.JavaCompile

plugins {
    id("socialismus.platform-bukkit")
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}

dependencies {
    compileOnly(libs.bundles.paper)
    implementation(libs.attache.paper)
}

tasks.withType<ShadowJar>().configureEach {
    archiveClassifier.set("PAPER")

    manifest {
        attributes["Plugin-Type"] = "PAPER"
    }
}
