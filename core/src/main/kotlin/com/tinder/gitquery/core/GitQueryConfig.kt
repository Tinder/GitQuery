/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core

import java.util.Collections.emptyMap

data class GitQueryConfig(
    var branch: String = "master",
    var files: Map<String, Any> = emptyMap(),
    var remote: String = "",
    var schema: GitQueryConfigSchema = GitQueryConfigSchema()
)
