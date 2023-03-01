package com.bkahlert.hello.props.data

import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.auth.Session.UnauthorizedSession
import com.bkahlert.kommons.auth.UserInfo
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.oauth.OAuth2ResourceServer
import com.tunjid.mutator.Mutation
import com.tunjid.mutator.coroutines.StateFlowProducer
import com.tunjid.mutator.coroutines.stateFlowProducer
import com.tunjid.mutator.mutation
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest
import kotlinx.serialization.json.JsonObject
import kotlin.time.Duration.Companion.seconds

public data class PropsRepository(
    private val sessionFlow: Flow<Resource<Session>>,
    private val propsDataSourceProvider: (AuthorizedSession) -> PropsDataSource,
    private val externalScope: CoroutineScope,
) {
    public constructor(
        propsDataSource: PropsDataSource,
        externalScope: CoroutineScope,
    ) : this(
        sessionFlow = flowOf(Success(object : AuthorizedSession {
            override val userInfo: UserInfo get() = error("unexpected mock invocation")
            override val diagnostics: Map<String, String?> get() = error("unexpected mock invocation")
            override suspend fun reauthorize(httpClient: HttpClient?): Session = error("unexpected mock invocation")
            override suspend fun unauthorize(httpClient: HttpClient?): UnauthorizedSession = error("unexpected mock invocation")
            override fun installAuth(config: HttpClientConfig<HttpClientEngineConfig>, vararg resources: OAuth2ResourceServer) {
                error("unexpected mock invocation")
            }
        })),
        propsDataSourceProvider = { propsDataSource },
        externalScope,
    )

    private val logger by ConsoleLogging

    private val propsDataSourceFlow: Flow<Resource<PropsDataSource?>> = sessionFlow.mapLatest { sessionResource ->
        logger.grouping("PropsDataSource from session", sessionResource) {
            when (sessionResource) {
                is Success -> when (val session = sessionResource.data) {
                    is UnauthorizedSession -> Success(null)
                    is AuthorizedSession -> Success(propsDataSourceProvider(session))
                }

                is Failure -> Failure("Failed to load props data source", sessionResource.cause)
            }
        }
    }

    private val propsFlow: Flow<Resource<Props?>> = propsDataSourceFlow.transformLatest { propsDataSourceResource ->
        logger.grouping("Props from PropsDataSource", propsDataSourceResource) {
            when (propsDataSourceResource) {
                is Success -> when (val propsDataSource = propsDataSourceResource.data) {
                    null -> emit(Success(null))
                    else -> emit(Resource.load { propsDataSource.getAll() })
                }

                is Failure -> Failure("Failed to load props", propsDataSourceResource.cause)
            }
        }
    }

    private val propsChanges: Flow<Mutation<Resource<Props?>>> = propsFlow
        .map {
            mutation {
                logger.grouping("resetProps from $this to $it") { it }
            }
        }

    private val propsProducer: StateFlowProducer<Resource<Props?>> = externalScope.stateFlowProducer(
        initialState = Success(null),
        started = SharingStarted.WhileSubscribed(10.seconds),
        mutationFlows = listOf(
            propsChanges,
        )
    )

    public fun setProp(id: String, value: JsonObject) {
        propsProducer.launch {
            mutate {
                logger.grouping("setProp", id, value) {
                    when (this) {
                        is Success -> when (val props = data) {
                            null -> Success(null)
                            else -> Success(props + (id to value))
                        }

                        is Failure -> this
                    }
                }
            }
        }
    }

    public fun removeProp(id: String) {
        propsProducer.launch {
            mutate {
                logger.grouping("removeProp", id) {
                    when (this) {
                        is Success -> when (val props = data) {
                            null -> Success(null)
                            else -> Success(props - id)
                        }

                        is Failure -> this
                    }
                }
            }
        }
    }

    public fun propsFlow(): StateFlow<Resource<Props?>> = propsProducer.state
}
