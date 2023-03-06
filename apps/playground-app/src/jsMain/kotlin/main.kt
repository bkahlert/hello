import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.demo.ClickUpDemoProvider
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.demo.HelloDemoProviders
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.hello.environment.ui.EnvironmentView
import com.bkahlert.hello.props.data.SessionPropsDataSource
import com.bkahlert.hello.session.data.OpenIDConnectSessionDataSource
import com.bkahlert.hello.session.data.SessionRepository
import com.bkahlert.hello.session.ui.SessionView
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.devmode.DevMode
import com.bkahlert.kommons.dom.FragmentParameters
import com.bkahlert.kommons.dom.LocationFragmentParameters
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.DemoView
import com.bkahlert.semanticui.demo.SemanticUiDemoProviders
import com.bkahlert.semanticui.demo.asDemoViewState
import com.bkahlert.semanticui.devmode.ComposeDevSession
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.circular
import com.bkahlert.semanticui.element.primary
import com.bkahlert.semanticui.element.size
import com.bkahlert.semanticui.module.updateDebugSettings
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.maxHeight
import org.jetbrains.compose.web.css.overflowX
import org.jetbrains.compose.web.css.overflowY
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Hr
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import playground.architecture.ArchitectureDemoProvider
import playground.clickupapp.ClickUpAppDemoProvider
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@JsModule("./semantic/semantic.less")
private external val SemanticStyles: dynamic

@JsModule("./styles/playground-app.scss")
private external val PlaygroundStyles: dynamic

fun main() {
    SemanticStyles
    PlaygroundStyles

    updateDebugSettings { module, settings ->
        settings.debug = true || module !in listOf("transition")
        settings.verbose = true
        settings.performance = true
    }

    val rootElement = document.getElementById("root").unsafeCast<HTMLDivElement>()

    val devMode = DevMode(name = "playground") {
        val rootSibling = document.createElement("div").unsafeCast<HTMLDivElement>().apply {
            classList.add("dev-session")
            checkNotNull(rootElement.parentNode).insertBefore(this, rootElement)
        }

        ComposeDevSession(rootSibling) {
            Div({
                classes("p-6", "mx-auto", "bg-white", "rounded-xl", "shadow-lg", "flex", "items-center", "space-x-4")
                style {
                    maxHeight(35.vh)
                    overflowX("hidden")
                    overflowY("scroll")
                }
            }) {
                Demo("Using Environment") { demoScope ->
                    val environmentRepository = remember { EnvironmentRepository(DynamicEnvironmentDataSource(), demoScope) }
                    val environmentResource by environmentRepository.environmentFlow().collectAsState(null)
                    when (val currentEnvironmentResource = environmentResource) {
                        null -> Loader("Loading environment")
                        is Success -> {
                            val sessionDataSource = remember { OpenIDConnectSessionDataSource(currentEnvironmentResource.data) }
                            val sessionRepository = remember { SessionRepository(sessionDataSource, demoScope) }
                            val sessionResource by sessionRepository.sessionFlow().collectAsState(null)
                            when (val currentSessionResource = sessionResource) {
                                null -> Loader("Loading session")
                                is Success -> {
                                    when (val session = currentSessionResource.data) {
                                        is Session.UnauthorizedSession -> P { Text("Unauthorized") }
                                        is Session.AuthorizedSession -> {
                                            val propsDataSource = remember { SessionPropsDataSource(session, currentEnvironmentResource.data) }
                                            var attempt: Any? by remember { mutableStateOf(null) }
                                            LaunchedEffect(propsDataSource) {
                                                try {
                                                    attempt = propsDataSource.getAll()
                                                } catch (ex: CancellationException) {
                                                    throw ex
                                                } catch (ex: Throwable) {
                                                    attempt = ex
                                                }
                                            }
                                            Header { Text("Response") }
                                            Pre {
                                                Code {
                                                    Text(attempt.toString())
                                                }
                                            }
                                        }
                                    }

                                    SessionView(currentSessionResource.data)
                                }

                                is Failure -> ErrorMessage(currentSessionResource.cause)
                            }

                            EnvironmentView(currentEnvironmentResource.data)
                        }

                        is Failure -> ErrorMessage(currentEnvironmentResource.message, currentEnvironmentResource.cause)
                    }
                }

            }
        }
    }


    renderComposable(rootElement) {
        Style(ClickUpStyleSheet)

        DemoView(
            ClickUpAppDemoProvider,
            *HelloDemoProviders,
            ClickUpDemoProvider,
            ArchitectureDemoProvider,
            *SemanticUiDemoProviders,
            state = LocationFragmentParameters(window).asDemoViewState("demo"),
        ) {
            val activationState: State<Boolean> = devMode.activationFlow.collectAsState()
            S("ui", "statistic") {
                S("value") { Text(if (activationState.value) "Active" else "Inactive") }
                S("label") { Hr() }
                BasicButton({
                    v.primary().size(Mini).circular()
                    onClick { devMode.toggle(it.nativeEvent) }
                }) {
                    Text("DevMode")
                }
            }
        }
    }
}

@Composable
public fun FragmentParameters.collectAsState(
    name: String,
    context: CoroutineContext = EmptyCoroutineContext,
): State<List<String>?> = produceState(getAll(name), this, context) {
    if (context == EmptyCoroutineContext) {
        asFlow(name).collect { value = it }
    } else withContext(context) {
        asFlow(name).collect { value = it }
    }
}

@Composable
public fun FragmentParameters.collectAsMutableState(
    name: String,
    context: CoroutineContext = EmptyCoroutineContext,
): MutableState<List<String>?> {
    val downStream: State<List<String>?> = produceState(getAll(name), this, context) {
        if (context == EmptyCoroutineContext) {
            asFlow(name).collect { value = it }
        } else withContext(context) {
            asFlow(name).collect { value = it }
        }
    }
    return remember {
        object : MutableState<List<String>?> {
            override var value: List<String>?
                get() = downStream.value
                set(value) {
                    setAll(name, value)
                }

            override fun component1(): List<String>? = value
            override fun component2(): (List<String>?) -> Unit = { value = it }
        }
    }
//    val result = remember { mutableStateOf(downStream.value) }
//    LaunchedEffect(result) {
//        snapshotFlow { result.value }
//            .collect { setAll(name, it) }
//    }
//    return result
}
