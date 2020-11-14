package com.tinder.gitquery

import com.tinder.gitquery.core.defaultBranch
import com.tinder.gitquery.core.defaultCleanOutput
import com.tinder.gitquery.core.defaultConfigFilename
import com.tinder.gitquery.core.defaultGradleRepoDir
import com.tinder.gitquery.core.defaultOutputDir
import com.tinder.gitquery.core.defaultRemote
import com.tinder.gitquery.core.defaultSha
import com.tinder.gitquery.core.defaultVerbose

abstract class GitQueryDefaultExtension {
    var configFile: String = defaultConfigFilename
    var remote: String = defaultRemote
    var branch: String = defaultBranch
    var repoDir: String = defaultGradleRepoDir
    var cleanOutput: Boolean = defaultCleanOutput
    var outputDir: String = defaultOutputDir
    var sha: String = defaultSha
    var verbose: Boolean = defaultVerbose
}
