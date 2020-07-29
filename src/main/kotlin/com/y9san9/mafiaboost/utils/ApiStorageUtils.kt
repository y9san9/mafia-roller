package com.y9san9.mafiaboost.utils

import java.io.File


class ApiStorage(private val file: File){
    init { file.refresh() }

    fun get() = try {
        file.readText().split(":").let { (id, hash) -> id.toInt() to hash }
    } catch (_: Throwable) { null }

    fun put(id: Int, hash: String) = file.writeText("$id:$hash")

    fun delete() = file.delete()
}
