/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import com.tinder.gitquery.core.DEFAULT_AUTO_SYNC
import org.gradle.api.Project

/**
 * Contains the settings for our plugin.
 */
open class GitQuerySyncExtension(open val project: Project) : GitQueryDefaultExtension() {
    var autoSync: Boolean = DEFAULT_AUTO_SYNC
}
