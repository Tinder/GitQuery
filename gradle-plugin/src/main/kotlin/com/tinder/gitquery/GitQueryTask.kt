/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.GitQueryConfig
import com.tinder.gitquery.core.GitQueryCore.updateConfig
import com.tinder.gitquery.core.GitQueryCore.sync
import com.tinder.gitquery.core.GitQueryCore.toAbsolutePath
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
open class GitQueryTask @Inject constructor(extension: GitQueryExtension) : DefaultTask() {

    // The url of the remote Git repo
    @Input
    val remote: String = extension.remote

    // The branch of the remote Git repo
    @Input
    val branch: String = extension.branch

    // The relative (to projectDir) path to a yaml file that describe a set of files to fetch/sync from a given repository.
    @Input
    val configFile: String = extension.configFile

    // The relative (to projectDir) path to a directory where the files should be synced to.
    @Input
    val outputDir: String = extension.outputDir

    // An path relative to buildDir, where the remote repo(s) can be cloned locally and stored (until cleaned)
    @Input
    val repoDir: String = extension.repoDir

    // An boolean directing to clean (remote all files) the output folder prior to running. (default: true)
    @Input
    val cleanOutput: Boolean = extension.cleanOutput

    // An boolean to enable showing the underlying commands and their outputs in the console. (default: false)
    @Input
    val verbose: Boolean = extension.verbose

    // A list of globs to use to generate the config file.
    @Input
    val generateGlobs: String = extension.generateGlobs

    init {
        group = "build"
        description = "Fetch the proto files defined in the gitquery.yml file"
    }

    @TaskAction
    fun syncRepo() {
        val config = GitQueryConfig.load(
            toAbsolutePath(
                path = configFile,
                prefixPath = "${project.projectDir}"
            )
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
        if (generateGlobs.isNotBlank()) {
            updateConfig(configFile = configFile, config = config, verbose = verbose)
        } else {
            config.cleanOutput = config.cleanOutput && cleanOutput
            sync(config = config, verbose = verbose)
        }
    }
}
