/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery

import org.gradle.api.Project

/**
 * Contains the settings for our plugin.
 */
open class GitQuerySyncExtension(open val project: Project) : GitQueryDefaultExtension()
