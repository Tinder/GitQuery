/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.defaultCleanOutput
import com.tinder.gitquery.core.defaultConfigFilename
import com.tinder.gitquery.core.defaultGradleRepoDir
import com.tinder.gitquery.core.defaultOutputDir
import com.tinder.gitquery.core.defaultVerbose
import org.gradle.api.Project

/**
 * Contains the settings for our plugin.
 */
open class GitQueryExtension(val project: Project) {
    var configFile: String = defaultConfigFilename
    var outputDir: String = defaultOutputDir
    var repoDir: String = defaultGradleRepoDir
    var cleanOutput: Boolean = defaultCleanOutput
    var verbose: Boolean = defaultVerbose
}
