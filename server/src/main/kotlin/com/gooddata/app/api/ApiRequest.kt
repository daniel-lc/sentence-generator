package com.gooddata.app.api

import com.gooddata.app.core.data.WordCategory

class EmptyRequest {

    override fun toString(): String = "{}"
}
data class WordCategoryRequest(val wordCategory: WordCategory)

object RequestPrinter {

    fun print(request: EmptyRequest): String = request.toString()
    fun print(request: WordCategoryRequest): String = request.toString()
}
