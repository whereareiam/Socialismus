plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("SocialismusDatabase")
    // Its API, and the libraries that API exposes, reach the modules that depend on SocialismusDatabase.
    exports.addAll(
        "me.whereareiam.socialismus.module.database.api",
        "me.whereareiam.dialectica",
        "me.whereareiam.strata.adapter.database",
        "org.jdbi"
    )
}

dependencies {
    implementation(projects.moduleDatabaseApi)
    implementation(projects.moduleDatabaseCommon)

    compileOnly(libs.jdbi.core)
    compileOnly(libs.dialectica.adapter.jdbi)
}
