plugins {
    id("module-api")
}

group = "me.whereareiam.socialismus.module"

toolkitPublish {
    artifactId.set("SocialismusNetwork")

    pom {
        name.set("SocialismusNetwork")
        description.set("Public API for SocialismusNetwork - the network Socialismus servers and proxies share state over")
    }

    javadoc {
        title.set("SocialismusNetwork API")
        windowTitle.set("SocialismusNetwork API")
    }
}
