/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.defaultFlatFiles
import com.tinder.gitquery.core.defaultIncludeGlobs
import org.gradle.api.Project

/**
 * Contains the settings for the gitquery initialize plugin that helps init and update a gitquery config file.
 */
open class GitQueryInitializeExtension(val project: Project) : GitQueryDefaultExtension() {
    var includeGlobs: List<String> = defaultIncludeGlobs
    var excludeGlobs: List<String> = defaultIncludeGlobs
    var flatFiles: Boolean = defaultFlatFiles
}
