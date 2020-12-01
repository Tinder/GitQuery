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
import org.gradle.api.Project

/**
 * Contains the settings for our plugin.
 */
open class GitQuerySyncExtension(open val project: Project) {
    var autoSync: Boolean = DEFAULT_AUTO_SYNC
    var branch: String = DEFAULT_BRANCH
    var cleanOutput: Boolean = DEFAULT_CLEAN_OUTPUT
    var configFile: String = DEFAULT_CONFIG_FILENAME
    var outputDir: String = DEFAULT_OUTPUT_DIR
    var remote: String = DEFAULT_REMOTE
    var repoDir: String = DEFAULT_GRADLE_REPO_DIR
    var verbose: Boolean = DEFAULT_VERBOSE
}
