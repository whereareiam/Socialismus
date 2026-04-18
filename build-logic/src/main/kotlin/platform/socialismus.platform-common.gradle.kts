plugins {
    id("socialismus.platform-base")
}

dependencies {
    add("implementation", project(":socialismus-api"))
    add("implementation", project(":integration-bstats"))
}
