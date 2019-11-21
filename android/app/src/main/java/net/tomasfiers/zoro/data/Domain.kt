package net.tomasfiers.zoro.data

data class Collection (
    val id: String,
    val version: Long,
    val name: String,
    val parent: Collection?
)
