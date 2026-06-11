import me.whereareiam.attache.plugin.gradle.extension.AttacheExtension

plugins {
    id("shared")
    alias(libs.plugins.attache)
}

dependencies {
    compileOnly(project(":socialismus-module:module-api"))

    implementation(libs.attache.standalone)

    attache(libs.guice)
    attache(libs.configura)
    attache(libs.configura.feature.extension)
    attache(libs.configura.feature.postprocess)
    attache(libs.configura.feature.polymorphic)
    attache(libs.commandant)
    attache(libs.keystone)
}

extensions.configure<AttacheExtension>("attache") {
    library(libs.guice) {
        relocate("com{}google{}inject", "me.whereareiam.socialismus.library.guice")
        relocate("com{}google{}common", "me.whereareiam.socialismus.library.guava")
    }

    library(libs.configura) {
        relocate("com{}fasterxml{}jackson", "me.whereareiam.socialismus.library.jackson")
        relocate("org{}yaml{}snakeyaml", "me.whereareiam.socialismus.library.snakeyaml")
    }

    library(libs.configura.feature.extension) {
        relocate("com{}fasterxml{}jackson", "me.whereareiam.socialismus.library.jackson")
        relocate("org{}yaml{}snakeyaml", "me.whereareiam.socialismus.library.snakeyaml")
    }

    library(libs.configura.feature.postprocess) {
        relocate("com{}fasterxml{}jackson", "me.whereareiam.socialismus.library.jackson")
        relocate("org{}yaml{}snakeyaml", "me.whereareiam.socialismus.library.snakeyaml")
    }

    library(libs.configura.feature.polymorphic) {
        relocate("com{}fasterxml{}jackson", "me.whereareiam.socialismus.library.jackson")
        relocate("org{}yaml{}snakeyaml", "me.whereareiam.socialismus.library.snakeyaml")
    }
}
