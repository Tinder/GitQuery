/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.tinder.gitquery.core.GitQueryConfig
import com.tinder.gitquery.core.GitQueryCore
import com.tinder.gitquery.core.defaultBranch
import com.tinder.gitquery.core.defaultCleanOutput
import com.tinder.gitquery.core.defaultConfigFilename
import com.tinder.gitquery.core.defaultFlatFiles
import com.tinder.gitquery.core.defaultOutputDir
import com.tinder.gitquery.core.defaultRemote
import com.tinder.gitquery.core.defaultRepoDir
import com.tinder.gitquery.core.defaultVerbose

class Cli : CliktCommand() {
    private val configFile: String by option(
        help =
        """A yaml file that describe a set of files to query and sync from a given repository. 
                |default: $defaultConfigFilename""".trimMargin()
    ).default(defaultConfigFilename)

    // Override attributes. The following attributes override the same value in config is they are defined.
    private val remote: String by option(
        help =
        """Remote Git repo url. 
                |If provided, this will override any value specified for [remote] in the [configFile].  
                |default: $defaultRemote""".trimMargin()
    ).default("")
    private val branch: String by option(
        help =
        """Remote Git repo branch. 
                |If provided, this will override any value specified for [branch] in the [configFile].  
                |default: $defaultBranch""".trimMargin()
    ).default("")
    private val outputDir: String by option(
        help =
        """Path to a directory where the files should be synced to. 
                |If provided, this will override any value specified for [outputDir] in the [configFile]. 
                |default: $defaultOutputDir""".trimMargin()
    ).default("")
    private val repoDir: String by option(
        help =
        """Where the remote repo(s) can be cloned locally and stored. 
                |If provided, this will override any value specified for [repoDir] in the [configFile].  
                |default: $defaultRepoDir""".trimMargin()
    ).default("")
    private val cleanOutput: Boolean by option(
        help =
        """Whether to clean (remote all files) the output folder prior to running. 
                |If set to true, this will override a false value specified for [cleanOutput] in the [configFile]. 
                |default: $defaultCleanOutput""".trimMargin()
    ).flag(default = defaultCleanOutput)
    private val initConfig: Boolean by option(
        help =
        """Initialize/update the config file based on command line params. Use --include-globs and --exclude-globs.
            |If the config-file exists, it will be updated. 
            |If the config file does not exist, it will be created with values from command line or internal defaults. 
                |default: false""".trimMargin()
    ).flag(default = false)
    private val includeGlobs: String by option(
        help =
        """A list of globs to include when generating/updating the config file.
                |If provided, this comma, space or pipe globs in the string value of this option 
|               |will be used to generate the config's `files` map.""".trimMargin()
    ).default("")
    private val excludeGlobs: String by option(
        help =
        """A list of globs to exclude when generating/updating the config file.
                |If provided, this comma, space or pipe globs in the string value of this option 
|               |will be used to exclude patterns when generating the config's `files` map.""".trimMargin()
    ).default("")
    private val nestedOutput: Boolean by option(
        help =
        """When --generate-globs is used, this option helps choose if the files in
                |the generated config file should be in a flat map or a nest map.
                |default: $defaultFlatFiles""".trimMargin()
    ).flag(default = defaultFlatFiles)
    private val verbose: Boolean by option(
        help =
        """Show the underlying commands and their outputs in the console.
                |default: $defaultVerbose""".trimMargin()
    ).flag(default = defaultVerbose)
    override fun run() {
        val config = GitQueryConfig.load(configFile, includeGlobs.isNotBlank())
        if (remote.isNotBlank()) {
            config.remote = remote
        }
        if (branch.isNotBlank()) {
            config.branch = branch
        }
        if (repoDir.isNotBlank()) {
            config.repoDir = repoDir
        }
        if (outputDir.isNotBlank()) {
            config.outputDir = outputDir
        }
        if (initConfig && includeGlobs.isNotBlank()) {
            config.includeGlobs = includeGlobs.split(",", " ", "|")
            config.excludeGlobs = excludeGlobs.split(",", " ", "|")
            config.flatFiles = nestedOutput
            GitQueryCore.updateConfig(configFile = configFile, config = config, verbose = verbose)
        } else {
            config.cleanOutput = config.cleanOutput && cleanOutput
            GitQueryCore.sync(config = config, verbose = verbose)
        }
    }
}

fun main(args: Array<String>) = Cli().main(args)
