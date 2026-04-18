import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("socialismus.platform-runtime")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    add("implementation", project(":platform-bukkit-common"))
    add("implementation", project(":integration-placeholderapi"))
    add("compileOnly", libs.findLibrary("cloud-paper").get())
    add("compileOnly", libs.findLibrary("brigadier").get())
}

tasks.named<Copy>("processResources").configure {
    filter<ReplaceTokens>(
        "tokens" to mapOf(
            "projectName" to rootProject.name,
            "projectVersion" to project.version
        )
    )
}
