/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.*
import org.gradle.api.Project

/**
 * Contains the settings for our plugin.
 */
open class GitQueryExtension(val project: Project) {
    var branch: String = defaultBranch
    var cleanOutput: Boolean = defaultCleanOutput
    var configFile: String = defaultConfigFilename
    var outputDir: String = defaultOutputDir
    var remote: String = defaultRemote
    var repoDir: String = defaultGradleRepoDir
    var verbose: Boolean = defaultVerbose
}
