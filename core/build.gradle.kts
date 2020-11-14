/*
 * © 2019 Match Group, LLC.
 */

plugins {
    java
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

dependencies {
    kotlinLibrary()
    implementation(Libs.snakeyaml)

    testImplementation(Libs.junit)
}

val generateVersionClass by tasks.register<Exec>("generateVersionClass") {
    workingDir(project.rootDir)

    commandLine("./build-support/bin/write-version-class")
}

tasks.getByName("compileKotlin").dependsOn(generateVersionClass)
