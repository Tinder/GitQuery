/*
 * © 2019 Match Group, LLC.
 */

plugins {
    java
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.snakeyaml)
}

object Versions {
    val snakeyaml = "1.25"
}

object Libs {
    val snakeyaml = "org.yaml:snakeyaml:${Versions.snakeyaml}"
}
