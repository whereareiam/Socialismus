plugins {
    id("module-api")
}

group = "me.whereareiam.socialismus.module"

dependencies {
    // A schema is Dialectica tables and Strata migrations, and rows are read through Dialectica's entity access.
    // SocialismusDatabase exports these packages to the modules that depend on it.
    compileOnlyApi(libs.dialectica)
    compileOnlyApi(libs.dialectica.adapter.jdbi)
    compileOnlyApi(libs.strata.adapter.database)

    testImplementation(libs.dialectica.bundle)
    testImplementation(libs.strata.adapter.database.dialectica)
}

toolkitPublish {
    artifactId.set("SocialismusDatabase")

    pom {
        name.set("SocialismusDatabase")
        description.set("Public API for SocialismusDatabase - the database Socialismus modules keep their data in")
    }

    javadoc {
        title.set("SocialismusDatabase API")
        windowTitle.set("SocialismusDatabase API")
    }
}
