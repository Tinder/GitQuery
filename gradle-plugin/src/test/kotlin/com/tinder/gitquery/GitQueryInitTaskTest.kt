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

class GitQueryInitTaskTest {

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
    fun taskGitQueryInitShouldCreateGitQueryConfig() {
        testProjectDir.apply {
            newFile("build.gradle").appendText(getBuildGradleSetup())
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("gitQueryInit")
            .withPluginClasspath()
            .build()

        assert(result.task(":gitQueryInit")?.outcome == TaskOutcome.SUCCESS)
        assert(result.output.contains("GitQuery: init complete"))
        assert(File("${testProjectDir.root}/gitquery.yml").exists())
    }

    @Test
    fun taskGitQueryInitShouldUpdateGitQueryConfig() {
        testProjectDir.apply {
            newFile("build.gradle").appendText(getBuildGradleSetup())
            newFile("gitquery.yml").appendText(getContentConfig())
        }

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("gitQueryInit")
            .withPluginClasspath()
            .build()

        assert(result.task(":gitQueryInit")?.outcome == TaskOutcome.SUCCESS)
        assert(result.output.contains("GitQuery: init complete"))
        assert(File("${testProjectDir.root}/gitquery.yml").exists())
    }

    private fun getBuildGradleSetup() =
        """
plugins {
  id 'com.tinder.gitquery'
}

gitQuery {
    configFile =  "gitquery.yml"
    repoDir = "tmp/remote"
    branch = "master"
    remote = "https://github.com/aminghadersohi/ProtoExample.git"
}

gitQueryInit {
    includeGlobs = ["**/*.proto"]
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
}
