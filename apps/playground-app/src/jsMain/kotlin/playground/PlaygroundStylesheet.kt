package playground

import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.semanticui.custom.cssColorValue
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.backgroundAttachment
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize

object PlaygroundStylesheet : StyleSheet() {
    val bg by style {
        backgroundAttachment("fixed")
        backgroundColor(RGB(0x231F20).cssColorValue)
        backgroundImage(
            "url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNDQwIiBoZWlnaHQ9IjI1MCIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSIgeG1sbnM6dj0iaHR0cHM6Ly92ZWN0YS5pby9uYW5vIj48ZGVmcz48bGluZWFyR3JhZGllbnQgeDE9IjAlIiB5MT0iMTAwJSIgeDI9IjEwMCUiIHkyPSIwJSIgaWQ9IkEiPjxzdG9wIHN0b3AtY29sb3I9InJnYmEoMjU1LCAyMTUsIDM4LCAxKSIgb2Zmc2V0PSIwIi8+PHN0b3Agc3RvcC1vcGFjaXR5PSIwIiBzdG9wLWNvbG9yPSJyZ2JhKDI1NSwgMjE1LCAzOCwgMSkiIG9mZnNldD0iLjY2Ii8+PC9saW5lYXJHcmFkaWVudD48bGluZWFyR3JhZGllbnQgeDE9IjEwMCUiIHkxPSIxMDAlIiB4Mj0iMCUiIHkyPSIwJSIgaWQ9IkIiPjxzdG9wIHN0b3AtY29sb3I9InJnYmEoMjU1LCAyMTUsIDM4LCAxKSIgb2Zmc2V0PSIwIi8+PHN0b3Agc3RvcC1vcGFjaXR5PSIwIiBzdG9wLWNvbG9yPSJyZ2JhKDI1NSwgMjE1LCAzOCwgMSkiIG9mZnNldD0iLjY2Ii8+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PHBhdGggZmlsbD0iIzIzMWYyMCIgZD0iTTAgMGgxNDQwdjI1MEgweiIvPjxnIGZpbGw9InVybCgjQSkiPjxwYXRoIGQ9Ik0zOCAyNTBMMjg4IDBoMTAwLjVsLTI1MCAyNTB6Ii8+PHBhdGggZD0iTTI3Ny42IDI1MGwyNTAtMjUwaDIzNS41bC0yNTAgMjUweiIvPjxwYXRoIGQ9Ik01MDcuMiAyNTBsMjUwLTI1MGgxNjAuNWwtMjUwIDI1MHoiLz48cGF0aCBkPSJNNzM1LjggMjUwbDI1MC0yNTBoMTA4bC0yNTAgMjUweiIvPjwvZz48ZyBmaWxsPSJ1cmwoI0IpIj48cGF0aCBkPSJNMTQzOSAyNTBMMTE4OSAwSDk1My41bDI1MCAyNTB6Ii8+PHBhdGggZD0iTTExOTkuNCAyNTBMOTQ5LjQgMEg4MDkuOWwyNTAgMjUweiIvPjxwYXRoIGQ9Ik05NDAuOCAyNTBMNjkwLjggMEgzNzQuM2wyNTAgMjUweiIvPjxwYXRoIGQ9Ik03MDUuMiAyNTBMNDU1LjIgMGgtMjMxbDI1MCAyNTB6Ii8+PC9nPjxwYXRoIGQ9Ik0xMjAyLjEzMiAyNTBMMTQ0MCAxMi4xMzJWMjUweiIgZmlsbD0idXJsKCNBKSIvPjxwYXRoIGQ9Ik0wIDI1MGgyMzcuODY4TDAgMTIuMTMyeiIgZmlsbD0idXJsKCNCKSIvPjwvc3ZnPg==')"
        )
        backgroundPosition("center bottom")
        backgroundRepeat("no-repeat")
        backgroundSize("contain")
    }
}