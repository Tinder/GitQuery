/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.tinder.gitquery.core.GIT_QUERY_VERSION
import com.tinder.gitquery.core.GitQueryInit
import com.tinder.gitquery.core.GitQuerySync
import com.tinder.gitquery.core.config.DEFAULT_BRANCH
import com.tinder.gitquery.core.config.DEFAULT_CLEAN_OUTPUT
import com.tinder.gitquery.core.config.DEFAULT_CONFIG_FILENAME
import com.tinder.gitquery.core.config.DEFAULT_FLAT_FILES
import com.tinder.gitquery.core.config.DEFAULT_OUTPUT_DIR
import com.tinder.gitquery.core.config.DEFAULT_REMOTE
import com.tinder.gitquery.core.config.DEFAULT_REPO_DIR
import com.tinder.gitquery.core.config.DEFAULT_VERBOSE
import com.tinder.gitquery.core.config.GitQueryConfig

class GitQueryCli : CliktCommand() {
    init {
        versionOption(GIT_QUERY_VERSION, message = { version -> "GitQuery $version" })
    }

    private val configFile: String by option(
        help = """A yaml file that describe a set of files to query and sync from a given repository. 
                |default: $DEFAULT_CONFIG_FILENAME""".trimMargin()
    ).default(DEFAULT_CONFIG_FILENAME)

    // Override attributes. The following attributes override the same value in config is they are defined.
    private val remote: String by option(
        help = """Remote Git repo url. 
                |If provided, this will override any value specified for [remote] in the [configFile].  
                |default: $DEFAULT_REMOTE""".trimMargin()
    ).default("")

    private val branch: String by option(
        help = """Remote Git repo branch. 
                |If provided, this will override any value specified for [branch] in the [configFile].  
                |default: $DEFAULT_BRANCH""".trimMargin()
    ).default("")

    private val outputDir: String by option(
        help = """Path to a directory where the files should be synced to. 
                |If provided, this will override any value specified for [outputDir] in the [configFile]. 
                |default: $DEFAULT_OUTPUT_DIR""".trimMargin()
    ).default("")

    private val repoDir: String by option(
        help = """Where the remote repo(s) can be cloned locally and stored. 
                |If provided, this will override any value specified for [repoDir] in the [configFile].  
                |default: $DEFAULT_REPO_DIR""".trimMargin()
    ).default("")

    private val dontCleanOutput: Boolean by option(
        help = """Whether to not clean the output folder prior to sync. 
                |If set, this will override the [cleanOutput] in the [configFile]
                |default: ${!DEFAULT_CLEAN_OUTPUT}""".trimMargin()
    ).flag(default = !DEFAULT_CLEAN_OUTPUT)

    private val initConfig: Boolean by option(
        help = """Initialize/update the config file based on command line params. 
                |Use --include-globs and --exclude-globs.
                |If [configFile] exists, it will be updated, else it will be created with values 
                |from command line or internal defaults. 
                |default: false""".trimMargin()
    ).flag(default = false)

    private val includeGlobs: String by option(
        help = """A list of globs to include when generating/updating the files attribute in [configFile].
                |If provided, this comma, space or pipe separated list of globs will override [includeGlobs] 
                |in [configFile] and used when initializing/updating the config's [files] map.""".trimMargin()
    ).default("")

    private val excludeGlobs: String by option(
        help = """A list of globs to exclude when generating/updating the files attribute in [configFile].
                |If provided, this comma, space or pipe separated list of globs will override [excludeGlobs]
                |in [configFile] and used to exclude patterns when initializing/updating
                |the config's [files] map.""".trimMargin()
    ).default("")

    private val flatFiles: Boolean by option(
        help = """When --generate-globs is used, this option helps choose if the files in
                |the generated config file should be in a flat map or a nest map.
                |default: $DEFAULT_FLAT_FILES""".trimMargin()
    ).flag(default = DEFAULT_FLAT_FILES)

    private val revision: String by option(
        help = """A revision to use when --init-config is used, 
                |if not provided the revision of latest [branch] is used""".trimMargin()
    ).default("")

    private val verbose: Boolean by option(
        help = """Show the underlying commands and their outputs in the console.
                |default: $DEFAULT_VERBOSE""".trimMargin()
    ).flag(default = DEFAULT_VERBOSE)

    override fun run() {
        val config = GitQueryConfig.load(configFile, initConfig)
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
        config.cleanOutput = !dontCleanOutput
        if (initConfig) {
            if (includeGlobs.isNotBlank()) {
                config.initConfig.includeGlobs = includeGlobs.split(",", " ", "|")
            }
            if (excludeGlobs.isNotBlank()) {
                config.initConfig.excludeGlobs = excludeGlobs.split(",", " ", "|")
            }
            if (revision.isNotBlank()) {
                config.initConfig.revision = revision
            }
            config.initConfig.flatFiles = flatFiles

            GitQueryInit.initConfig(configFile = configFile, config = config, verbose = verbose)
        } else {
            GitQuerySync.sync(config = config, verbose = verbose)
        }
    }
}

fun main(args: Array<String>) = GitQueryCli().main(args)
