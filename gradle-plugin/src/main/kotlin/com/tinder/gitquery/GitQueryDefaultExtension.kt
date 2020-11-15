package com.tinder.gitquery

import com.tinder.gitquery.core.DEFAULT_BRANCH
import com.tinder.gitquery.core.DEFAULT_CLEAN_OUTPUT
import com.tinder.gitquery.core.DEFAULT_CONFIG_FILENAME
import com.tinder.gitquery.core.DEFAULT_GRADLE_REPO_DIR
import com.tinder.gitquery.core.DEFAULT_OUTPUT_DIR
import com.tinder.gitquery.core.DEFAULT_REMOTE
import com.tinder.gitquery.core.DEFAULT_SHA
import com.tinder.gitquery.core.DEFAULT_VERBOSE

open class GitQueryDefaultExtension {
    var configFile: String = DEFAULT_CONFIG_FILENAME
    var remote: String = DEFAULT_REMOTE
    var branch: String = DEFAULT_BRANCH
    var repoDir: String = DEFAULT_GRADLE_REPO_DIR
    var cleanOutput: Boolean = DEFAULT_CLEAN_OUTPUT
    var outputDir: String = DEFAULT_OUTPUT_DIR
    var sha: String = DEFAULT_SHA
    var verbose: Boolean = DEFAULT_VERBOSE
}
