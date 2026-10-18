import me.whereareiam.attache.plugin.gradle.extension.AttacheExtension

plugins {
    id("module-java")
    alias(libs.plugins.attache)
}

dependencies {
    compileOnly(projects.moduleDatabaseApi)

    // Loaded into the module's class loader; socialismusModule exports Jdbi, Dialectica and Strata's database adapter to
    // the modules that depend on SocialismusDatabase.
    attache(libs.jdbi.core)
    attache(libs.dialectica.bundle)
    attache(libs.dialectica.adapter.jdbi)
    attache(libs.strata.adapter.database)
    attache(libs.strata.adapter.database.jdbi)
    attache(libs.strata.adapter.database.dialectica)
    attache(libs.hikaricp)

    testImplementation(projects.moduleDatabaseApi)
    testImplementation(libs.jdbi.core)
    testImplementation(libs.dialectica.bundle)
    testImplementation(libs.dialectica.adapter.jdbi)
    testImplementation(libs.strata.adapter.database)
    testImplementation(libs.strata.adapter.database.jdbi)
    testImplementation(libs.strata.adapter.database.dialectica)
    testImplementation(libs.hikaricp)
    testImplementation(libs.sqlite)
    testImplementation(libs.postgresql)
    testImplementation(libs.mariadb)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.mariadb)
}

extensions.configure<AttacheExtension>("attache") {
    library(libs.dialectica.adapter.jdbi) {
        // Dialectica comes from the bundle, in the version SocialismusDatabase declares.
        excludeTransitive("me.whereareiam", "dialectica-api")
        excludeTransitive("me.whereareiam", "dialectica-common")
    }

    library(libs.strata.adapter.database.dialectica) {
        // Dialectica comes from the bundle, in the version SocialismusDatabase declares.
        excludeTransitive("me.whereareiam", "dialectica")
        excludeTransitive("me.whereareiam", "dialectica-api")
        excludeTransitive("me.whereareiam", "dialectica-common")
    }

    // One group per DatabaseType, named in lower case: only the configured database's driver is downloaded.
    group("sqlite") {
        library(libs.sqlite)
    }

    group("mysql") {
        library(libs.mariadb) {
            excludeTransitive("com.github.waffle", "waffle-jna")
            excludeTransitive("net.java.dev.jna", "jna")
            excludeTransitive("net.java.dev.jna", "jna-platform")
        }
    }

    group("postgres") {
        library(libs.postgresql) {
            excludeTransitive("com.github.waffle", "waffle-jna")
            excludeTransitive("net.java.dev.jna", "jna")
            excludeTransitive("net.java.dev.jna", "jna-platform")
        }
    }
}
