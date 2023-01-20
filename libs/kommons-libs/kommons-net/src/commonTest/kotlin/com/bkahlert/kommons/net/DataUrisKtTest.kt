package com.bkahlert.kommons.net

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DataUrisKtTest {

    @Test
    fun svg() = testAll {
        DataUri.Svg(SVG_MARKUP).toString() shouldBe SVG_DATA_URI
    }
}

// language=svg
val SVG_MARKUP = """
    <svg version="1.1"
         width="300" height="200"
         xmlns="http://www.w3.org/2000/svg">
      <rect width="100%" height="100%" fill="red" />
      <circle cx="150" cy="100" r="80" fill="green" />
      <text x="150" y="125" font-size="60" text-anchor="middle" fill="white">SVG</text>
    </svg>
""".trimIndent()

const val SVG_DATA_URI = "" +
    "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiCiAgICAgd2" +
    "lkdGg9IjMwMCIgaGVpZ2h0PSIyMDAiCiAgICAgeG1sbnM9Imh0dHA6Ly93d3" +
    "cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2" +
    "h0PSIxMDAlIiBmaWxsPSJyZWQiIC8+CiAgPGNpcmNsZSBjeD0iMTUwIiBjeT" +
    "0iMTAwIiByPSI4MCIgZmlsbD0iZ3JlZW4iIC8+CiAgPHRleHQgeD0iMTUwIi" +
    "B5PSIxMjUiIGZvbnQtc2l6ZT0iNjAiIHRleHQtYW5jaG9yPSJtaWRkbGUiIG" +
    "ZpbGw9IndoaXRlIj5TVkc8L3RleHQ+Cjwvc3ZnPg"
