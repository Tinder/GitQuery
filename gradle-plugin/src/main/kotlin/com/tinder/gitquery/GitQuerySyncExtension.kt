/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.*
import org.gradle.api.Project

/**
 * Contains the settings for our plugin.
 */
open class GitQuerySyncExtension(open val project: Project) {
    var configFile: String = defaultConfigFilename
    var remote: String = defaultRemote
    var branch: String = defaultBranch
    var repoDir: String = defaultGradleRepoDir
    var cleanOutput: Boolean = defaultCleanOutput
    var outputDir: String = defaultOutputDir
    var verbose: Boolean = defaultVerbose
}
