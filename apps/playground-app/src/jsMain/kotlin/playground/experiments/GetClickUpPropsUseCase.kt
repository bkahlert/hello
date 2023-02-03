package playground.experiments

import com.bkahlert.hello.clickup.client.http.PersonalAccessToken
import com.bkahlert.hello.props.domain.GetPropUseCase
import com.bkahlert.hello.props.domain.GetPropsRepositoryUseCase
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.json.LenientJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

public class GetClickUpPropsUseCase(
    private val getPropUseCase: GetPropUseCase,
) {
    public constructor(
        getPropsRepositoryUseCase: GetPropsRepositoryUseCase,
    ) : this(GetPropUseCase(getPropsRepositoryUseCase))

    public operator fun invoke(): Flow<ClickUpProps?> {
        val clickUpPropsFlow: () -> Flow<ClickUpProps?> = {
            getPropUseCase("clickup").map { clickUpProps ->
                if (clickUpProps == null) null
                else when (val apiToken = (clickUpProps as? JsonObject)?.get("api-token")) {
                    null -> ClickUpProps(null)
                    else -> ClickUpProps(LenientJson.decodeFromJsonElement(apiToken))
                }
            }
        }
        return console.grouping(GetClickUpPropsUseCase::class.simpleName!!, block = clickUpPropsFlow)
    }
}

data class ClickUpProps(
    val apiToken: PersonalAccessToken?,
)
