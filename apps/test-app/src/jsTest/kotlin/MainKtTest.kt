import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import org.jetbrains.compose.web.testutils.runTest
import org.w3c.dom.asList
import kotlin.test.Test

class MainKtTest {

    @Test
    fun compose() = runTest {
        composition {
            Counter()
        }
        root.querySelectorAll("button").asList() should { buttons ->
            buttons.shouldHaveSize(2)
            buttons.map { it.textContent }.shouldContainExactly("-", "+")
        }
    }
}
