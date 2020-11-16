/*
 * © 2019 Match Group, LLC.
 */

plugins {
    application
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
    id("edu.sc.seis.macAppBundle") version "2.1.0"
}

application {
    mainClass.set("com.tinder.gitquery.cli.GitQueryCliKt")
}

dependencies {
    kotlinLibrary()
    implementation(project(":core"))
    implementation(Libs.clikt)
}

macAppBundle {
    mainClassName = "com.tinder.gitquery.cli.GitQueryCliKt"
    icon = "myIcon.icns"
    bundleJRE = true
    javaProperties["apple.laf.useScreenMenuBar"] = "true"
    backgroundImage = "doc/macbackground.png"
}
