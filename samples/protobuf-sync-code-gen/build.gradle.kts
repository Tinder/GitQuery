
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    application
    id("com.google.protobuf") version "0.8.13"
    id("com.tinder.gitquery") version "3.0.0-SNAPSHOT"
    java
}

val protoDir = "src/main/proto"

gitQuery {
    autoSync = true
    cleanOutput = true
    configFile = "gitquery.yml"
    outputDir = protoDir
    repoDir = "${project.buildDir}/tmp/.gitquery"
}

// This will init/update the config file every time the gitQuery task runs.
gitQueryInit {
    flatFiles = false
    includeGlobs = listOf("**/examples/addressbook.proto")
    excludeGlobs = listOf("**/generated/**/*.proto")
    revision = "v3.14.0"
}

sourceSets {
    main {
        proto.srcDirs(protoDir)
    }
}

val protobufVersion by extra("3.14.0")

repositories {
    jcenter()
    if (System.getenv("CI") == "true") {
        mavenLocal()
    }
    maven("https://jitpack.io")
}

application {
    mainClassName = "com.examples.addressbook.MainKt"
    applicationName = "addressbook"
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.14.0")
}

protobuf {
    generatedFilesBaseDir = "$projectDir/src"
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    generateProtoTasks {
        ofSourceSet("main").forEach { task ->
            if (task.name == "generateProto") {
                task.dependsOn(tasks.getByName("gitQuery"))
            }
        }
    }
}
