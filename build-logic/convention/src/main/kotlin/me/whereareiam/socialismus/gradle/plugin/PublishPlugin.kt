package me.whereareiam.socialismus.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import java.net.URI

class PublishPlugin : Plugin<Project> {
    private fun getVersionType(): String {
        val version = System.getenv("VERSION") ?: "dev"
        val isDevVersion = version.contains("dev", true)
        return if (isDevVersion) "development" else "release"
    }

    private fun getRealm(): String {
        val publishRealm = System.getenv("PUBLISH_REALM")
        return (publishRealm ?: getVersionType()).lowercase()
    }

    override fun apply(target: Project) {
        target.pluginManager.withPlugin("maven-publish") {
            target.extensions.configure<PublishingExtension> {
                repositories {
                    maven {
                        val realm = getRealm()
                        url = URI.create("https://maven.whereareiam.me/$realm")
                        credentials {
                            username = System.getenv("PUBLISH_USER") ?: ""
                            password = System.getenv("PUBLISH_TOKEN") ?: ""
                        }
                    }
                }
            }
        }
    }
}