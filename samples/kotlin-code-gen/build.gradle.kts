import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import com.google.protobuf.gradle.remove

plugins {
    application
    id("com.google.protobuf") version "0.8.12"
    id("com.tinder.gitquery") version "3.0.0-SNAPSHOT"
    java
}

val protoDir = "src/main/proto"

gitQuery {
    cleanOutput = true
    configFile = "gitquery.yml"
    outputDir = protoDir
    repoDir = "tmp/.gitquery"
}

gitQueryInit {
    branch = "master"
    cleanOutput = true
    configFile = "gitquery.yml"
    flatFiles = false
    includeGlobs = listOf("**/examples/addressbook.proto")
    outputDir = protoDir
    remote = "git@github.com:protocolbuffers/protobuf.git"
    repoDir = "tmp/.gitquery"
    sha = "v3.13.0.1"
}

sourceSets {
    main {
        proto.srcDirs(protoDir)
    }
}

val protobufVersion by extra("3.13.0")

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
    implementation("com.google.protobuf:protobuf-java:3.13.0")
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
