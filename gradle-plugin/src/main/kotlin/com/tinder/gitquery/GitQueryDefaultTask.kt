package com.tinder.gitquery

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input

abstract class GitQueryDefaultTask(extension: GitQueryDefaultExtension) : DefaultTask() {

    /*
    The relative (to projectDir) path to a yaml file that describe a set of files to fetch/sync from a given
    repository.
    */
    @Input
    val configFile: String = extension.configFile

    /* The remote repository to query files from. */
    @Input
    val remote: String = extension.remote

    /*
    The single branch that will be cloned on first run and pulled incrementally on subsequent
    runs. The sha values used in [commits] and [files] must be available under [branch].
    */
    @Input
    val branch: String = extension.branch

    /* A directory to hold the intermediate cloned git repo. */
    @Input
    val repoDir: String = extension.repoDir

    /* A directory to sync the queried files into. */
    @Input
    val outputDir: String = extension.outputDir

    /* If true [default], cleans out the output folder prior to running sync. */
    @Input
    val cleanOutput: Boolean = extension.cleanOutput

    /* An boolean to enable showing the underlying commands and their outputs in the console. (default: false) */
    @Input
    val verbose: Boolean = extension.verbose
}
