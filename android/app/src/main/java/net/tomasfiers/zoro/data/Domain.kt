package net.tomasfiers.zoro.data

data class Collection (
    val id: String,
    var version: Long,
    var name: String,
    var parent: Collection?
)
