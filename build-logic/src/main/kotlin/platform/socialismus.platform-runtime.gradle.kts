import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("socialismus.platform-base")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    add("implementation", project(":socialismus-adapter-command"))
    add("implementation", project(":socialismus-adapter-module"))
    add("implementation", project(":socialismus-api"))
    add("implementation", project(":socialismus-common"))
    add("implementation", project(":integration-bstats"))
    add("implementation", project(":integration-packetevents"))
    add("compileOnly", libs.findBundle("cloud").get())
}
