package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import com.bkahlert.hello.search.Engine
import com.bkahlert.hello.search.Search

@Composable
fun SearchDemo() {
    Demos("Search") {
        Demo("not empty + full search + focus") {
            Search(engine = Engine.Default, query = "all engines (focussed)", fullSearch = true)
        }
        Demo("not empty + full search") {
            Search(engine = Engine.Default, query = "all engines", fullSearch = true)
        }
        Demo("not empty") {
            Search(engine = Engine.Default, query = "single engine")
        }
        Demo("empty + full search") {
            Search(engine = Engine.Default, fullSearch = true)
        }
        Demo("empty") {
            Search(engine = Engine.Default, fullSearch = false)
        }
    }
}
