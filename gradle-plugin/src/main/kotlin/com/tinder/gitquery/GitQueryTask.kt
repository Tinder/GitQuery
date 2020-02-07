/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.GitQueryCore.sync
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * A Gradle task that given a yaml file, describing a set of files in a remote git repo, and an
 * intermediate (repo) directory, query, fetch and sync those files into a given output directory.
 *
 * For more details see the README.md file in the root of this project.
 */
open class GitQueryTask @Inject constructor(extension: GitQueryExtension) : DefaultTask() {

    // The relative (to projectDir) path to a yaml file that describe a set of files to fetch/sync from a given repository.
    @Input
    val configFile: String = extension.configFile

    // The relative (to projectDir) path to a directory where the files should be synced to.
    @Input
    val outputDir: String = extension.outputDir

    // An path relative to buildDir, where the remote repo(s) can be cloned locally and stored (until cleaned)
    @Input
    val repoDir: String = extension.repoDir

    init {
        group = "build"
        description = "Fetch the proto files defined in the config.yaml file"
    }

    @TaskAction
    fun syncRepo() {
        sync(
            "${project.projectDir}/$configFile",
            "${project.buildDir}/$repoDir",
            "${project.projectDir}/$outputDir"
        )
    }
}
