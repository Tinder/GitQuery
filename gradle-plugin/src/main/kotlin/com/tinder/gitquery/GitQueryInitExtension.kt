/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.config.DEFAULT_FLAT_FILES
import com.tinder.gitquery.core.config.DEFAULT_REVISION
import com.tinder.gitquery.core.config.defaultIncludeGlobs
import org.gradle.api.Project

/**
 * Contains the settings for the gitquery initialize plugin that helps init and update a gitquery config file.
 */
open class GitQueryInitExtension(val project: Project) {
    var includeGlobs: List<String> = defaultIncludeGlobs
    var excludeGlobs: List<String> = defaultIncludeGlobs
    var flatFiles: Boolean = DEFAULT_FLAT_FILES
    var revision: String = DEFAULT_REVISION
}
