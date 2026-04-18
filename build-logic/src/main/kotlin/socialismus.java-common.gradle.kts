import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.compile.JavaCompile

plugins {
    java
    `maven-publish`
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val buildVersion = providers.environmentVariable("VERSION").orElse("dev")

group = "me.whereareiam"
version = buildVersion.get()

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

dependencies {
    add("compileOnly", libs.findLibrary("lombok").get())
    add("annotationProcessor", libs.findLibrary("lombok").get())

    add("compileOnly", libs.findLibrary("guice").get())
    add("compileOnly", libs.findLibrary("annotations").get())
    add("compileOnly", libs.findLibrary("configura").get())
    add("compileOnly", libs.findLibrary("commandant").get())
    add("compileOnly", libs.findLibrary("keystone").get())
    add("compileOnly", libs.findBundle("adventure").get())
    add("implementation", libs.findLibrary("attache-common").get())

    add("testImplementation", libs.findLibrary("configura").get())
    add("testImplementation", libs.findLibrary("commandant").get())
    add("testImplementation", libs.findLibrary("keystone").get())
    add("testImplementation", libs.findLibrary("guice").get())
    add("testImplementation", libs.findBundle("testing").get())
    add("testRuntimeOnly", libs.findLibrary("junit-platform").get())
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            val realm = providers.environmentVariable("PUBLISH_REALM")
                .orElse(
                    buildVersion.map { versionString ->
                        if (versionString.contains("dev", ignoreCase = true)) "development" else "release"
                    }
                )
                .get()
                .lowercase()

            url = uri("https://maven.whereareiam.me/$realm")

            credentials {
                username = providers.environmentVariable("PUBLISH_USER").orNull.orEmpty()
                password = providers.environmentVariable("PUBLISH_TOKEN").orNull.orEmpty()
            }
        }
    }
}
