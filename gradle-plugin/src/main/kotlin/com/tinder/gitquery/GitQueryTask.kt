/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.GitQueryInit.initConfig
import com.tinder.gitquery.core.GitQuerySync
import com.tinder.gitquery.core.config.GitQueryConfig
import com.tinder.gitquery.core.utils.toAbsolutePath
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * A Gradle task that given a yaml file, describing a set of files in a remote git repo, and an
 * intermediate (repo) directory, query, fetch and sync those files into a given output directory.
 *
 * For more details see the README.md file in the root of this project.
 *
 * @param extension [GitQueryExtension]
 */
open class GitQueryTask @Inject constructor(extension: GitQueryExtension, initExtension: GitQueryInitExtension) :
    DefaultTask() {

    /* A list of globs to include when generating the config file. */
    @Input
    val includeGlobs: List<String> = initExtension.includeGlobs

    /* A list of globs to exclude when generating the config file. */
    @Input
    val excludeGlobs: List<String> = initExtension.excludeGlobs

    /*
    If true [default], when --init-config is used, the files attribute
    in the resulted saved config file will be a flat map of filename to sha values.
    If false, it will be a tree of directories as parent nodes and files as leaf nodes.
    */
    @Input
    val flatFiles: Boolean = initExtension.flatFiles

    /*
    A revision under [branch], when --init-config is applied, that will be used as the default revision for files.
    */
    @Input
    val revision: String = initExtension.revision

    /**
     * The relative (to projectDir) path to a yaml file that describe a set of files to fetch/sync from a given
     * repository.
     */
    @Input
    val configFile: String = extension.configFile

    /** The remote repository to query files from. */
    @Input
    val remote: String = extension.remote

    /**
     * The single branch that will be cloned on first run and pulled incrementally on subsequent
     * runs. The revision values used in [commits] and [files] must be available under [branch].
     */
    @Input
    val branch: String = extension.branch

    /** A directory to hold the intermediate cloned git repo. */
    @Input
    val repoDir: String = extension.repoDir

    /** A directory to sync the queried files into. */
    @Input
    val outputDir: String = extension.outputDir

    /** If true [default], cleans out the output folder prior to running sync. */
    @Input
    val cleanOutput: Boolean = extension.cleanOutput

    /** An boolean to enable showing the underlying commands and their outputs in the console. (default: false) */
    @Input
    val verbose: Boolean = extension.verbose

    init {
        group = "build"
        description = "Sync the files listed in the config file"
    }

    @TaskAction
    fun taskAction() {

        if (includeGlobs.isNotEmpty()) {
            val config = readConfig(createIfNotExists = true)
            config.initConfig.includeGlobs = includeGlobs
            if (excludeGlobs.isNotEmpty()) {
                config.initConfig.excludeGlobs = excludeGlobs
            }
            if (revision.isNotBlank()) {
                config.initConfig.revision = revision
            }
            config.initConfig.flatFiles = flatFiles
            initConfig(
                configFile = configFileName(),
                config = config,
                verbose = verbose
            )
        }

        GitQuerySync.sync(config = readConfig(createIfNotExists = true), verbose = verbose)
    }

    private fun readConfig(createIfNotExists: Boolean): GitQueryConfig {
        val config = GitQueryConfig.load(
            filename = configFileName(),
            createIfNotExists = createIfNotExists
        )
        if (remote.isNotBlank()) {
            config.remote = remote
        }
        if (branch.isNotBlank()) {
            config.branch = branch
        }
        config.repoDir = toAbsolutePath(
            path = if (repoDir.isNotBlank()) {
                repoDir
            } else {
                config.repoDir
            },
            prefixPath = "${project.buildDir}"
        )
        config.outputDir = toAbsolutePath(
            path = if (outputDir.isNotBlank()) {
                outputDir
            } else {
                config.outputDir
            },
            prefixPath = "${project.projectDir}"
        )
        config.cleanOutput = cleanOutput
        return config
    }

    private fun configFileName(): String {
        return toAbsolutePath(
            path = configFile,
            prefixPath = "${project.projectDir}"
        )
    }
}
