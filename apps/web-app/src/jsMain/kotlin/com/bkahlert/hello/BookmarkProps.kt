package com.bkahlert.hello

import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.uri.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

data class BookmarkProps(
    val default: Uri?,
) {
    companion object {
        fun Flow<Resource<JsonObject?>>.mapBookmarkProps(): Flow<Resource<BookmarkProps?>> = map { bookmarkPropsResource ->
            when (bookmarkPropsResource) {
                is Success -> when (val bookmarkProps = bookmarkPropsResource.data) {
                    null -> Success(null)
                    else -> when (val default = bookmarkProps["default"]) {
                        null -> Success(BookmarkProps(null))
                        else -> Success(BookmarkProps(LenientJson.decodeFromJsonElement(default)))
                    }
                }

                is Failure -> Failure("Failed to load bookmark settings", bookmarkPropsResource.cause)
            }
        }
    }
}
