/*
 * © 2019 Match Group, LLC.
 */

plugins {
    application
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

application {
    mainClass.set("com.tinder.gitquery.cli.MainKt")
}

dependencies {
    kotlinLibrary()
    implementation(project(":core"))
    implementation(Libs.clikt)
}
