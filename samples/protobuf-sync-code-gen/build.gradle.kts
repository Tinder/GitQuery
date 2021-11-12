
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    application
    id("com.google.protobuf") version "0.8.17"
    id("com.tinder.gitquery") version "3.0.11"
    java
}

val protoDir = "src/main/proto"

gitQuery {
    autoSync = true
    configFile = "gitquery.yml"
    outputDir = protoDir
    repoDir = "tmp/.gitquery"
}

sourceSets {
    main {
        proto.srcDirs(protoDir)
    }
}

repositories {
    jcenter()
    if (System.getenv("CI") == "true") {
        mavenLocal()
    }
    maven("https://jitpack.io")
}

application {
    mainClass.set("com.examples.addressbook.MainKt")
    applicationName = "addressbook"
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.19.1")
}

protobuf {
    generatedFilesBaseDir = "$projectDir/src"
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.1"
    }
    generateProtoTasks {
        ofSourceSet("main").forEach { task ->
            if (task.name == "generateProto") {
                task.dependsOn(tasks.getByName("gitQuery"))
            }
        }
    }
}
