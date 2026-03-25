package me.whereareiam.socialismus.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ModuleInfoPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.version = (System.getenv("VERSION") ?: "dev")
        target.group = "me.whereareiam"
    }
}