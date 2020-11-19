/*
 * © 2019 Match Group, LLC.
 */

package com.tinder.gitquery.core.config

/**
 * A model to encapsulate attributes related to init-config.
 */
data class GitQueryInitConfig(
    /*
    If true [default], when --init-config is used, the files attribute
    in the resulted saved config file will be a flat map of filename to revision values.
    If false, it will be a tree of directories as parent nodes and files as leaf nodes.
    */
    var flatFiles: Boolean = DEFAULT_FLAT_FILES,
    /* A list of globs to include when generating the config file. */
    var includeGlobs: List<String> = defaultIncludeGlobs,
    /* A list of globs to exclude when generating the config file. */
    var excludeGlobs: List<String> = defaultExcludeGlobs,
    /* A default revision, if empty, HEAD is used. */
    var revision: String = DEFAULT_REVISION
)
