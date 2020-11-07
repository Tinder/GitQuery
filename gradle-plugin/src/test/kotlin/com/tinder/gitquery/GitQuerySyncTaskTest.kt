/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class GitQuerySyncTaskTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    @Before
    fun setup() {
        testProjectDir.create()
    }

    @After
    fun tearDown() {
        testProjectDir.delete()
    }

    @Test
    fun taskCreateFolderWithFilesAtRoot() {
        testProjectDir.apply {
            newFolder("gitquery-output")
            newFile("build.gradle").appendText(getBuildGradleSetup())
            newFile("gitquery.yml").appendText(getContentConfig())
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("gitQuery")
            .withPluginClasspath()
            .build()
        assert(result.task(":gitQuery")?.outcome == TaskOutcome.SUCCESS)
        assert(result.output.contains("GitQuery: creating outputPath"))
        assert(File("${testProjectDir.root}/gitquery-output/definitions/user.proto").exists())
    }

    @Test
    fun missingRemoteInConfigFileIsThrowingException() {
        testProjectDir.apply {
            newFolder("gitquery-output")
            newFile("build.gradle").appendText(getBuildGradleSetup())
            newFile("gitquery.yml").appendText(getContentMissingRemoteConfig())
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("gitQuery")
            .withPluginClasspath()
            .buildAndFail()
        println(result.output)
        assert(result.task(":gitQuery")?.outcome == TaskOutcome.FAILED)
        assert(result.output.contains("Parameter remote may not be a blank string ()"))
    }

    @Test
    fun wrongConfigFormatThrowingException() {
        testProjectDir.apply {
            newFolder("gitquery-output")
            newFile("build.gradle").appendText(getBuildGradleSetup())
            newFile("gitquery.yml").appendText(getContentWrongFormat())
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("gitQuery")
            .withPluginClasspath()
            .buildAndFail()

        assert(result.output.contains("Can't construct a java object for tag:yaml.org"))
        assert(result.task(":gitQuery")?.outcome == TaskOutcome.FAILED)
    }

    private fun getBuildGradleSetup() =
        """
plugins {
  id 'com.tinder.gitquery'
}

gitQuery {
    configFile =  "gitquery.yml"
    outputDir =  "gitquery-output"
    repoDir = "tmp/remote"
}
        """.trimIndent()

    private fun getContentConfig() =
        """
---
schema:
  version: 1
remote: https://github.com/aminghadersohi/ProtoExample.git
branch: master
commits:
  latest: d654b510d2689e8ee56d23d03dff2be742737f86
files:
  README.md: latest
  definitions:
    user.proto: 42933446d0321958e8c12216d04b9f0c382ebf1b

        """.trimIndent()

    private fun getContentMissingRemoteConfig() =
        """
---
schema:
  version: 1
branch: master
files:
  definitions:
    user.proto: 42933446d0321958e8c12216d04b9f0c382ebf1b
        """.trimIndent()

    private fun getContentWrongFormat() =
        """---
master{ }
incorrectFormat
        """.trimIndent()
}
