import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    alias(libs.plugins.shadow)
}

subprojects {
    apply(plugin = "com.gradleup.shadow")

    tasks.withType<ShadowJar> {
        archiveBaseName.set(rootProject.name)

        manifest {
            attributes["Plugin-Type"] = archiveClassifier
        }

        relocate("com.alessiodp.libby", "me.whereareiam.socialismus.library.libby")
        relocate("org.bstats", "me.whereareiam.socialismus.library.bStats")

        relocate("org.yaml.snakeyaml", "me.whereareiam.socialismus.library.snakeyaml")
        relocate("com.google.common", "me.whereareiam.socialismus.library.guava")
        relocate("com.google.inject", "me.whereareiam.socialismus.library.guice")

        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }

    repositories {
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        "implementation"(project(":socialismus-integration:integration-packetevents"))
        "implementation"(project(":socialismus-integration:integration-bstats"))
        "implementation"(project(":socialismus-adapter-command"))
        "implementation"(project(":socialismus-adapter-config"))
        "implementation"(project(":socialismus-adapter-module"))
        "implementation"(project(":socialismus-platform"))
        "implementation"(project(":socialismus-common-api"))
        "implementation"(project(":socialismus-common"))
        "implementation"(project(":socialismus-shared"))

        "compileOnly"(rootProject.libs.bundles.cloud)
    }

    when (name) {
        "platform-paper", "platform-bukkit" -> {
            dependencies {
                "implementation"(project(":socialismus-integration:integration-placeholderapi"))
                "implementation"(rootProject.libs.bundles.bStats.bukkit)

                "compileOnly"(rootProject.libs.cloud.paper)
            }

            tasks.named<Copy>("processResources") {
                filter<ReplaceTokens>(
                    "tokens" to mapOf(
                        "projectName" to rootProject.name,
                        "projectVersion" to project.version
                    )
                )
            }
        }

        "platform-velocity" -> {
            dependencies {
                "implementation"(project(":socialismus-integration:integration-papiproxybridge"))
                "implementation"(project(":socialismus-integration:integration-valiobungee"))
                "implementation"(rootProject.libs.bundles.bStats.velocity)
            }
        }
    }

    tasks.named<Jar>("jar") {
        dependsOn("shadowJar")
    }
}
