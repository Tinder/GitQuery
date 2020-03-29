/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core

import java.util.Collections.emptyMap

/**
 * A model for the config yaml file.
 */
data class GitQueryConfig(
    var branch: String = "master",
    var files: Map<String, Any> = emptyMap(),
    var remote: String = "",
    var schema: GitQueryConfigSchema = GitQueryConfigSchema(),
    var commits: Map<String, String> = emptyMap()
)
