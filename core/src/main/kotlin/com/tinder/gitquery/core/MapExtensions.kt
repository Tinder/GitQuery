package com.tinder.gitquery.core

@Suppress("UNCHECKED_CAST")
internal fun java.util.HashMap<String, Any>.toSortedMap(): Map<String, Any> {
    return this.mapValues {
        if (it.value is HashMap<*, *>) {
            (it.value as HashMap<String, Any>).toSortedMap()
        } else {
            it.value
        }
    }.toSortedMap()
}

@Suppress("UNCHECKED_CAST")
internal fun java.util.HashMap<String, Any>.insertNested(relativePath: String, sha: String) {
    val file = relativePath.substringAfterLast("/")
    val path = relativePath.substringBeforeLast("/")
    if (path == file) {
        this[file] = sha
    } else {
        val pathFirst = path.substringBefore("/")
        val pathFirstMap = if (this.containsKey(pathFirst) && this[pathFirst] is HashMap<*, *>) {
            this[pathFirst] as HashMap<String, Any>
        } else {
            val pathFirstMap = HashMap<String, Any>()
            this[pathFirst] = pathFirstMap
            pathFirstMap
        }
        pathFirstMap.insertNested(relativePath.substringAfter("/"), sha)
    }
}
