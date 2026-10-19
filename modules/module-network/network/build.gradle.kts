plugins {
    id("packaging-module-jar")
}

socialismusModule {
    name.set("SocialismusNetwork")
    // Its API reaches the modules that depend on SocialismusNetwork: transports and the modules that replicate.
    exports.add("me.whereareiam.socialismus.module.network.api")
}

dependencies {
    implementation(projects.moduleNetworkApi)
    implementation(projects.moduleNetworkCommon)
}
