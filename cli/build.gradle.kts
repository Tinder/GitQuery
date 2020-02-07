/*
 * © 2019 Match Group, LLC.
 */

plugins {
    application
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

application {
    mainClassName = "com.tinder.gitquery.cli.MainKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":core"))
    implementation(Libs.clikt)
}

object Versions {
    var clikt = "2.3.0"
}

object Libs {
    val clikt = "com.github.ajalt:clikt:${Versions.clikt}"
}

