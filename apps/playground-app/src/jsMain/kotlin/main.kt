import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.demo.ClickUpDemoProvider
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.hello.demo.HelloDemoProviders
import com.bkahlert.kommons.devmode.DevMode
import com.bkahlert.kommons.dom.FragmentParameters
import com.bkahlert.kommons.dom.LocationFragmentParameters
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.core.updateDebugSettings
import com.bkahlert.semanticui.demo.DemoView
import com.bkahlert.semanticui.demo.SemanticUiDemoProviders
import com.bkahlert.semanticui.demo.asDemoViewState
import com.bkahlert.semanticui.devmode.ComposeDevSession
import com.bkahlert.semanticui.element.BasicButton
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Image
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.circular
import com.bkahlert.semanticui.element.primary
import com.bkahlert.semanticui.element.size
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.withContext
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Hr
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import playground.architecture.ArchitectureDemoProvider
import playground.clickupapp.ClickUpAppDemoProvider
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun main() {

    updateDebugSettings { module, settings ->
        settings.debug = true || module !in listOf("transition")
        settings.verbose = true
        settings.performance = true
    }

    val rootElement = document.getElementById("root").unsafeCast<HTMLDivElement>().apply {
        style.padding = "1em"
    }

    val devMode = DevMode(name = "playground") {
        val rootSibling = document.createElement("div").unsafeCast<HTMLDivElement>().apply {
            classList.add("dev-session")
            checkNotNull(rootElement.parentNode).insertBefore(this, rootElement)
        }

        ComposeDevSession(rootSibling) {
            var image by remember { mutableStateOf(ImageFixtures.BKAHLERTFavicon) }
            Segment {
                Button({
                    onClick {
                        image = when (image) {
                            ImageFixtures.BKAHLERTFavicon -> ImageFixtures.HelloFavicon
                            else -> ImageFixtures.BKAHLERTFavicon
                        }
                    }
                }) {
                    Image(image, attrs = { v.size(Small) })
                }
            }
        }
    }

    renderComposable(rootElementId = "root") {
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
