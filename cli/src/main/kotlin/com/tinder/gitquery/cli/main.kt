/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.tinder.gitquery.core.GitQueryCore
import com.tinder.gitquery.core.defaultConfigFilename
import com.tinder.gitquery.core.defaultOutputDir
import com.tinder.gitquery.core.defaultRepoDir
import com.tinder.gitquery.core.loadConfig

class Cli : CliktCommand() {
    private val configFile:
        String by option(
            help =
                """
                |A yaml file that describe a set of files to query and sync from a given repository. 
                |default: $defaultConfigFilename""".trimMargin()
        )
            .default("")
    private val outputDir:
        String by option(
            help =
                """Path to a directory where the files should be synced to. 
                |If provided, this will override any value specified for [outputDir] in the [configFile]. 
                |default: $defaultOutputDir""".trimMargin()
        )
            .default("")
    private val repoDir:
        String by option(
            help =
                """Where the remote repo(s) can be cloned locally and stored. 
                |If provided, this will override any value specified for [repoDir] in the [configFile].  
                |default: $defaultRepoDir""".trimMargin()
        )
            .default("")
    private val cleanOutput:
        Boolean by option(
            help =
                """Whether to clean (remote all files) the output folder prior to running. 
                |If set to true, this will override a false value specified for [cleanOutput] in the [configFile]. 
                |default: false""".trimMargin()
        )
            .flag(default = false)
    private val verbose:
        Boolean by option(
            help =
                """Show the underlying commands and their outputs in the console.
                |default: false""".trimMargin()
        )
            .flag(default = false)

    override fun run() {
        val config = loadConfig(configFile)
        if (repoDir.isNotBlank()) {
            config.repoDir = repoDir
        }
        if (outputDir.isNotBlank()) {
            config.outputDir = outputDir
        }
        config.cleanOutput = config.cleanOutput || cleanOutput

        GitQueryCore.sync(config = config, verbose = verbose)
    }
}

fun main(args: Array<String>) = Cli().main(args)
