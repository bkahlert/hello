package com.bkahlert.hello.app.ui

import com.bkahlert.kommons.uri.DataUri

public object HelloImageFixtures : Iterable<DataUri> {

    /** The [Hello!](https://github.com/bkahlert/hello) favicon as an [SVG](https://en.wikipedia.org/wiki/SVG). */
    public val HelloFavicon: DataUri by lazy { DataUri.parse(HELLO_FAVICON) }

    /** The [Hello!](https://github.com/bkahlert/hello) mark as an [SVG](https://en.wikipedia.org/wiki/SVG). */
    public val HelloMark: DataUri by lazy { DataUri.parse(HELLO_MARK) }

    override fun iterator(): Iterator<DataUri> = iterator {
        yield(HelloFavicon)
        yield(HelloMark)
    }
}

private const val HELLO_FAVICON = "" +
    "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC" +
    "9zdmciIGFyaWEtbGFiZWw9IkhlbGxvISIgcm9sZT0iaW1nIiBjdXJzb3I9ImRlZmF1bHQiIH" +
    "ZpZXdCb3g9IjAgMCAxMjggMTI4Ij4KICAgIDxwYXRoIGZpbGw9IiNjMjFmNzMiCiAgICAgIC" +
    "AgICBkPSJtMTEsMTE1VjEzYzAtMi4yMSwxLjc5LTQsNC00aDE4YzIuMjEsMCw0LDEuNzksNC" +
    "w0djM0YzAsMi4yMSwxLjc5LDQsNCw0aDM0YzIuMjEsMCw0LTEuNzksNC00VjEzYzAtMi4yMS" +
    "wxLjc5LTQsNC00aDE4YzIuMjEsMCw0LDEuNzksNCw0djEwMmMwLDIuMjEtMS43OSw0LTQsNG" +
    "gtMThjLTIuMjEsMC00LTEuNzktNC00di0zOWMwLTIuMjEtMS43OS00LTQtNGgtMzRjLTIuMi" +
    "wwLTQsMS44LTQsNHYzOWMwLDIuMjEtMS43OSw0LTQsNEgxNWMtMi4yMSwwLTQtMS43OS00LT" +
    "RaIi8+CiAgICA8ZyBmaWxsPSIjMjlhYWUyIj4KICAgICAgICA8cmVjdCB4PSI5MiIgeT0iOT" +
    "MiIHdpZHRoPSIyNiIgaGVpZ2h0PSIyNiIgcng9IjQiIHJ5PSI0Ii8+CiAgICAgICAgPHBhdG" +
    "ggZD0ibTkxLjUsMTNjLS4yLTIuMzUsMi4xNi00LDQuNTItNGgtLjAyczE3LjQ5LDAsMTcuND" +
    "ksMGMyLjM0LDAsNC4xOCwxLjk5LDMuOTksNC4zMi0xLjM0LDE2LjQtNC42Niw2NC4wNi00Lj" +
    "Y2LDY0LjA2LS4xOSwyLjA1LTEuOTEsMy42Mi0zLjk3LDMuNjJoLTguNTJjLTIuMDUsMC0zLj" +
    "c2LTEuNTUtMy45Ni0zLjU5LDAsMC0zLjQ3LTQ4LjMyLTQuODctNjQuNDEiLz4KICAgIDwvZz" +
    "4KPC9zdmc+Cg=="

private const val HELLO_MARK = "" +
    "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC" +
    "9zdmciIGFyaWEtbGFiZWw9IkhlbGxvISIgcm9sZT0iaW1nIiBjdXJzb3I9ImRlZmF1bHQiIH" +
    "ZpZXdCb3g9IjAgMCAxMjggMTI4Ij48c3R5bGU+PCFbQ0RBVEFbLkJ7bWl4LWJsZW5kLW1vZG" +
    "U6bXVsdGlwbHl9XV0+PC9zdHlsZT48cGF0aCBjbGFzcz0iQiIgZmlsbD0iI2MyMWY3MyIgZD" +
    "0ibTExLDExNVYxM2MwLTIuMjEsMS43OS00LDQtNGgxOGMyLjIxLDAsNCwxLjc5LDQsNHYzNG" +
    "MwLDIuMjEsMS43OSw0LDQsNGgzNGMyLjIxLDAsNC0xLjc5LDQtNFYxM2MwLTIuMjEsMS43OS" +
    "00LDQtNGgxOGMyLjIxLDAsNCwxLjc5LDQsNHYxMDJjMCwyLjIxLTEuNzksNC00LDRoLTE4Yy" +
    "0yLjIxLDAtNC0xLjc5LTQtNHYtMzljMC0yLjIxLTEuNzktNC00LTRoLTM0Yy0yLjIsMC00LD" +
    "EuOC00LDR2MzljMCwyLjIxLTEuNzksNC00LDRIMTVjLTIuMjEsMC00LTEuNzktNC00WiIvPj" +
    "xnIGNsYXNzPSJCIiBmaWxsPSIjMjlhYWUyIj48cmVjdCB4PSI5MiIgeT0iOTMiIHdpZHRoPS" +
    "IyNiIgaGVpZ2h0PSIyNiIgcng9IjQiIHJ5PSI0Ii8+PHBhdGggZD0ibTkxLjUsMTNjLS4yLT" +
    "IuMzUsMi4xNi00LDQuNTItNGgtLjAyczE3LjQ5LDAsMTcuNDksMGMyLjM0LDAsNC4xOCwxLj" +
    "k5LDMuOTksNC4zMi0xLjM0LDE2LjQtNC42Niw2NC4wNi00LjY2LDY0LjA2LS4xOSwyLjA1LT" +
    "EuOTEsMy42Mi0zLjk3LDMuNjJoLTguNTJjLTIuMDUsMC0zLjc2LTEuNTUtMy45Ni0zLjU5LD" +
    "AsMC0zLjQ3LTQ4LjMyLTQuODctNjQuNDEiLz48L2c+PC9zdmc+"
