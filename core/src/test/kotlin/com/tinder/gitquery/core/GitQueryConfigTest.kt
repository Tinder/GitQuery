package com.tinder.gitquery.core

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class GitQueryConfigTest {
    @get:Rule
    val testProjectDir = TemporaryFolder()

    @Before
    fun setup() {
        testProjectDir.create()
        testProjectDir.apply {
            newFile("gitquery.yml").appendText(getContentConfig())
        }
    }

    @After
    fun tearDown() {
        testProjectDir.delete()
    }

    @Test
    fun `loadConfig - given file exists, should return config`() {
        // When
        val config = GitQueryConfig.load("${testProjectDir.root}/gitquery.yml")

        // Then
        assert(config.remote == "https://github.com/aminghadersohi/ProtoExample.git")
        assert(config.branch == "master")
        assert(config.commits == mapOf("latest" to "d654b510d2689e8ee56d23d03dff2be742737f86"))
        assert(
            config.files == mapOf(
                "README.md" to "latest",
                "definitions" to mapOf("user.proto" to "42933446d0321958e8c12216d04b9f0c382ebf1b")
            )
        )
    }

    @Test
    fun `loadConfig - given blank filename, show throw`() {
        // Given
        val filename = "          "

        // When
        val actualError = kotlin.runCatching {
            GitQueryConfig.load(filename)
        }.exceptionOrNull()

        require(actualError is IllegalArgumentException)
        assert(
            actualError.message == "Input filename may not be a blank string ($filename)"
        )
    }

    @Test
    fun `loadConfig - given non existent filename, show throw`() {
        // Given
        val filename = "config.yaml"

        // When
        val actualError = kotlin.runCatching {
            GitQueryConfig.load(filename)
        }.exceptionOrNull()

        require(actualError is IllegalStateException)
        assert(actualError.message == "Config file does not exist ($filename)")
    }
}

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
