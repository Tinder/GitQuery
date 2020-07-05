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
