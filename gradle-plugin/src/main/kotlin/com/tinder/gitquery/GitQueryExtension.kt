/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.config.DEFAULT_AUTO_SYNC
import com.tinder.gitquery.core.config.DEFAULT_BRANCH
import com.tinder.gitquery.core.config.DEFAULT_CLEAN_OUTPUT
import com.tinder.gitquery.core.config.DEFAULT_CONFIG_FILENAME
import com.tinder.gitquery.core.config.DEFAULT_GRADLE_REPO_DIR
import com.tinder.gitquery.core.config.DEFAULT_OUTPUT_DIR
import com.tinder.gitquery.core.config.DEFAULT_REMOTE
import com.tinder.gitquery.core.config.DEFAULT_VERBOSE
import com.tinder.gitquery.core.config.GitQueryInitConfig
import org.gradle.api.Project

/**
 * Contains the settings for our plugin.
 */
open class GitQueryExtension(open val project: Project) {
    var initConfig: GitQueryInitConfig = GitQueryInitConfig()
    var configFile: String = DEFAULT_CONFIG_FILENAME
    var remote: String = DEFAULT_REMOTE
    var branch: String = DEFAULT_BRANCH
    var repoDir: String = DEFAULT_GRADLE_REPO_DIR
    var cleanOutput: Boolean = DEFAULT_CLEAN_OUTPUT
    var outputDir: String = DEFAULT_OUTPUT_DIR
    var verbose: Boolean = DEFAULT_VERBOSE
    var autoSync: Boolean = DEFAULT_AUTO_SYNC
}
