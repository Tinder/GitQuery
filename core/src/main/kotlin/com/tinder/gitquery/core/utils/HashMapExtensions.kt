package com.tinder.gitquery.core.utils

@Suppress("UNCHECKED_CAST")
internal fun HashMap<String, Any>.sortMap(): Map<String, Any> {
    return this.mapValues {
        if (it.value is HashMap<*, *>) {
            (it.value as HashMap<String, Any>).sortMap()
        } else {
            it.value
        }
    }.toSortedMap()
}

@Suppress("UNCHECKED_CAST")
internal fun HashMap<String, Any>.insertNested(relativePath: String, revision: String) {
    val file = relativePath.substringAfterLast("/")
    val path = relativePath.substringBeforeLast("/")
    if (path == file) {
        this[file] = revision
    } else {
        val pathFirst = path.substringBefore("/")
        val pathFirstMap = if (this.containsKey(pathFirst) && this[pathFirst] is HashMap<*, *>) {
            this[pathFirst] as HashMap<String, Any>
        } else {
            val pathFirstMap = HashMap<String, Any>()
            this[pathFirst] = pathFirstMap
            pathFirstMap
        }
        pathFirstMap.insertNested(relativePath.substringAfter("/"), revision)
    }
}
