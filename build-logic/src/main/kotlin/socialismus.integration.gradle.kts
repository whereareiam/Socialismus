plugins {
    id("socialismus.java-common")
}

dependencies {
    add("compileOnly", project(":socialismus-api"))
}
