/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.tinder.gitquery.core.GitQueryCore

class Cli : CliktCommand() {
    private val configFile:
            String by option(help = "a yaml file that describe a set of files to fetch/sync from a given repository")
        .default("config.yaml")
    private val outputDir:
            String by option(help = "path to a directory where the files should be synced to")
        .default("./synced-src")
    private val repoDir:
            String by option(help = "where the remote repo(s) can be cloned locally and stored")
        .default("./build/tmp/repo")

    override fun run() {
        GitQueryCore.sync(
            configFile = toAbsolutePath(configFile),
            repoPath = toAbsolutePath(repoDir),
            outputPath = toAbsolutePath(outputDir)
        )
    }
}

fun main(args: Array<String>) = Cli().main(args)

private fun toAbsolutePath(path: String) =
    if (path.isEmpty()) {
        System.getProperty("user.dir")
    } else {
        if (path[0] == '/') {
            path
        } else {
            System.getProperty("user.dir") + "/" + path
        }
    }
