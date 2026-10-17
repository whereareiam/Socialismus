import me.whereareiam.attache.plugin.gradle.extension.AttacheExtension

plugins {
    id("module-java")
    alias(libs.plugins.attache)
}

dependencies {
    compileOnly(projects.socialismusModule.moduleApi)

    implementation(libs.attache.standalone)

    attache(libs.guice)
    attache(libs.configura)
    attache(libs.configura.feature.extension)
    attache(libs.configura.feature.postprocess)
    attache(libs.configura.feature.polymorphic)
    attache(libs.commandant)
    attache(libs.keystone)
    attache(libs.strata)
    attache(libs.strata.adapter.configura)
}

extensions.configure<AttacheExtension>("attache") {
    library(libs.strata.adapter.configura) {
        // It asks for Configura 2.0.0; Socialismus ships its own, newer Configura. Attache keeps the dependencies of
        // an excluded library, so Configura's parts are excluded as well.
        excludeTransitive("me.whereareiam", "configura")
        excludeTransitive("me.whereareiam", "configura-api")
        excludeTransitive("me.whereareiam", "configura-common")
        relocate("com{}fasterxml{}jackson", "me.whereareiam.socialismus.library.jackson")
        relocate("org{}yaml{}snakeyaml", "me.whereareiam.socialismus.library.snakeyaml")
    }

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
