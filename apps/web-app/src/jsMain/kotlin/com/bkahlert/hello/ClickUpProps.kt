package com.bkahlert.hello

import com.bkahlert.hello.clickup.client.http.PersonalAccessToken
import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.kommons.json.LenientJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

data class ClickUpProps(
    val apiToken: PersonalAccessToken?,
) {
    companion object {
        fun Flow<Resource<JsonObject?>>.mapClickUpProps(): Flow<Resource<ClickUpProps?>> = map { clickUpPropsResource ->
            when (clickUpPropsResource) {
                is Success -> when (val clickUpProps = clickUpPropsResource.data) {
                    null -> Success(null)
                    else -> when (val apiToken = clickUpProps["api-token"]) {
                        null -> Success(ClickUpProps(null))
                        else -> Success(ClickUpProps(LenientJson.decodeFromJsonElement(apiToken)))
                    }
                }

                is Failure -> Failure("Failed to load ClickUp settings", clickUpPropsResource.cause)
            }
        }
    }
}
