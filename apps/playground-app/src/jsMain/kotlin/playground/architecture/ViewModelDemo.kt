package playground.architecture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.Message
import com.bkahlert.semanticui.collection.attached
import com.bkahlert.semanticui.collection.borderless
import com.bkahlert.semanticui.collection.info
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Attached.Bottom
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.element.Button
import com.bkahlert.semanticui.element.Buttons
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.attached
import com.bkahlert.semanticui.element.icon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text

@Composable
fun ViewModelDemo() {
    Demo("ViewModel: CounterApp") {
        CounterApp()
    }
}


@Composable
fun CounterApp(
    viewModel: CounterViewModel = rememberCounterViewModel(),
) {
    val counterState = viewModel.getCounterFlow().collectAsState(
        initial = Counter()
    )

    Menu({ v.borderless() }) {
        Item { Img(ImageFixtures.PearLogo.toString()) }
        Menu({ classes("right") }) {
            Item {
                Buttons({ v.icon() }) {
                    Button({
                        onClick { viewModel.incrementCounterLaunch() }
                    }) {
                        Icon("plus")
                    }
                    Button({
                        onClick { viewModel.decrementCounterLaunch() }
                    }) {
                        Icon("minus")
                    }
                }
            }
        }
    }

    Segment({ v.attached() }) {
        S("ui", "one", "statistics") {
            S("statistic") {
                S("value") { Text(counterState.value.value.toString()) }
                S("label") { Text("Counter") }
            }
        }
    }

    Message({ v.attached(Bottom).info() }) {
        Icon("info")
        S("ui", "label") {
            Text("Last action")
            S("detail") { Text(counterState.value.message) }
        }
    }
}

data class Counter(
    val value: Int = 0,
    val message: String = "Init",
)

interface CounterDataSource {
    suspend fun increment()
    suspend fun decrement()
    fun getCounterFlow(): Flow<Counter>
}

class CounterRepository(private val counterDataSource: CounterDataSource) {

    suspend fun increment() {
        counterDataSource.increment()
    }

    suspend fun decrement() {
        counterDataSource.decrement()
    }

    fun getCounterFlow(): Flow<Counter> {
        return counterDataSource.getCounterFlow()
    }
}

class GetCounterUseCase(private val repository: CounterRepository) {
    operator fun invoke(): Flow<Counter> = repository.getCounterFlow()
}

class DecrementCounterUseCase(private val repository: CounterRepository) {
    suspend operator fun invoke() {
        repository.decrement()
    }
}

class IncrementCounterUseCase(private val repository: CounterRepository) {
    suspend operator fun invoke() {
        repository.increment()
    }
}

class CounterViewModel(
    private val getCounter: GetCounterUseCase,
    private val incrementCounter: IncrementCounterUseCase,
    private val decrementCounter: DecrementCounterUseCase,
) {
    fun incrementCounterLaunch() {
        CoroutineScope(Dispatchers.Default).launch { incrementCounter() }
    }

    fun decrementCounterLaunch() {
        CoroutineScope(Dispatchers.Default).launch { decrementCounter() }
    }

    fun getCounterFlow(): Flow<Counter> {
        return getCounter()
    }
}

@Composable
fun rememberCounterViewModel(
    counterDataSource: CounterDataSource = InMemoryCounterDataSource(),
) = remember(counterDataSource) {
    CounterViewModel(
        getCounter = GetCounterUseCase(CounterRepository(counterDataSource)),
        incrementCounter = IncrementCounterUseCase(CounterRepository(counterDataSource)),
        decrementCounter = DecrementCounterUseCase(CounterRepository(counterDataSource)),
    )
}

class InMemoryCounterDataSource(
    private var counter: Counter = Counter(),
    private var counterFlow: MutableSharedFlow<Counter> = MutableSharedFlow(
        extraBufferCapacity = 2,
        onBufferOverflow = DROP_OLDEST
    ),
) : CounterDataSource {

    override suspend fun increment() {
        counter = counter.copy(
            value = counter.value + 1,
            message = "increment"
        )
        counterFlow.tryEmit(counter)
    }

    override suspend fun decrement() {
        counter = counter.copy(
            value = counter.value - 1,
            message = "decrement"
        )
        counterFlow.tryEmit(counter)
    }

    override fun getCounterFlow(): Flow<Counter> {
        return counterFlow.asSharedFlow()
    }
}
