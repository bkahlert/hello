package com.bkahlert.hello.search

import com.bkahlert.hello.search.SearchEngine.Google
import com.bkahlert.hello.ui.fmod
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.dom.URL

/**
 * Search engines, such as [Google].
 */
@Suppress("SpellCheckingInspection")
enum class SearchEngine(
    /**
     * Name of the search engine.
     */
    private val caption: String,
    /**
     * Brand color of the search engine.
     */
    val color: Color,
    /**
     * URL creator for a given query.
     */
    val url: (String) -> URL,
    /**
     * Name(s) of the icon [Semantic UI icon](https://semantic-ui.com/elements/icon.html).
     */
    vararg val icon: String,
) {
    /**
     * [Linguee](https://www.linguee.de)
     */
    Linguee("Linguee", RGB(0x0e2b46), {
        URL.parse("https://www.linguee.de/deutsch-englisch/search?source=auto&query=$it")
    }, "language"),

    /**
     * [Google](https://www.google.de)
     */
    Google("Google", RGB("#EC4A3A"), {
        URL.parse("https://www.google.de/search?q=$it")
    }, "google"),

    /**
     * [start.me](https://start.me)
     */
    StartMe("start.me", RGB("#4cb4e0"), { // #0093D4
        URL.parse("https://start.me/search/google?q=$it")
    }, "bookmark"),

    /**
     * [Bing](https://www.bing.com)
     */
    Bing("Bing", RGB("#02809D"), {
        URL.parse("https://www.bing.com/search?q=$it")
    }, "microsoft"),

    /**
     * [Yahoo](https://de.search.yahoo)
     */
    Yahoo("Yahoo", RGB("#542DFE"), {
        URL.parse("https://de.search.yahoo.com/yhs/search?p=$it")
    }, "yahoo"),

    /**
     * [Yandex Video](https://yandex.com/video)
     */
    YandexVideo("Yandex Video", RGB("#FF0202"), {
        URL.parse("https://yandex.com/video/search?text=$it)")
    }, "yandex"),

    /**
     * [Google Dataset Search](https://datasetsearch.research.google)
     */
    GoogleDataset("Google Dataset Search", RGB("#5F6367"), {
        URL.parse("https://datasetsearch.research.google.com/search?query=$it")
    }, "database"),

    /**
     * [Qwant](https://www.qwant.com)
     */
    Qwant("Qwant", RGB("#AF27CC"), {
        URL.parse("https://www.qwant.com/?q=$it")
    }, "eye", "slash"),

    /**
     * [Duck Duck Go](https://duckduckgo.com/?q)
     */
    @Suppress("GrazieInspection")
    DuckDuckGo("Duck Duck Go", RGB("#65BC46"), {
        URL.parse("https://duckduckgo.com/?q=$it")
    }, "eye", "slash"),

    /**
     * [Glyph Search](https://glyphsearch.com/?query)
     */
    GlyphSearch("glyphsearch", RGB("#2b3e51"), {
        URL.parse("https://glyphsearch.com/?query=$it")
    }, "images"),

    /**
     * [flaticon](https://www.flaticon.com)
     */
    FlatIcon("flaticon", RGB("#49d295"), {
        URL.parse("https://www.flaticon.com/search?license=selection&order_by=4&grid=small&word=$it")
    }, "images"),

    /**
     * [freepik](https://de.freepik.com)
     */
    FreePik("freepik", RGB("#0e60c8"), {
        URL.parse("https://de.freepik.com/search?dates=any&format=search&page=1&selection=1&sort=popular&query=$it")
    }, "images"),

    /**
     * [thenounproject](https://thenounproject.com/search)
     */
    TheNounProject("thenounproject", RGB("#000000"), {
        URL.parse("https://thenounproject.com/search/?q=$it")
    }, "images"),

    /**
     * [iconfinder](https://www.iconfinder.com)
     */
    IconFinder("iconfinder", RGB("#1a916c"), {
        URL.parse("https://www.iconfinder.com/search/?price=free&q=$it")
    }, "images"),

    /**
     * [iconmonstr](https://iconmonstr.com/?s)
     */
    IconMonstr("iconmonstr", RGB("#000000"), {
        URL.parse("https://iconmonstr.com/?s=$it")
    }, "images"),

    /**
     * [icons8](https://icons8.com/icons)
     */
    Icons8("icons8", RGB("#28b351"), {
        URL.parse("https://icons8.com/icons/set/$it")
    }, "images"),

    /**
     * [dryicons](https://dryicons.com/free)
     */
    DryIcons("dryicons", RGB("#eb4239"), {
        URL.parse("https://dryicons.com/free-icons/$it")
    }, "images"),

    /**
     * [vecteezy](https://www.vecteezy.com)
     */
    VectEezy("vecteezy", RGB("#f58400"), {
        URL.parse("https://www.vecteezy.com/free-vector/$it?license-free=true")
    }, "images"),

    /**
     * [iconspedia](https://www.iconspedia.com)
     */
    IconsPedia("iconspedia", RGB("#00bedb"), {
        URL.parse("https://www.iconspedia.com/search/$it/")
    }, "images"),

    /**
     * [fontello](http://fontello.com/#search)
     */
    Fontello("fontello", RGB("#c9312c"), {
        URL.parse("http://fontello.com/#search=$it")
    }, "images"),

    /**
     * [iconarchive](http://www.iconarchive.com)
     */
    IconArchive("iconarchive", RGB("#5c7ab8"), {
        URL.parse("http://www.iconarchive.com/search?q=$it")
    }, "images"),

    /**
     * [unsplash](https://unsplash.com/s)
     */
    Unsplash("unsplash", RGB("#111111"), {
        URL.parse("https://unsplash.com/s/photos/$it")
    }, "images"),

    /**
     * [DKB Confluence](https://confluence.dkb.ag)
     */
    DkbConfluence("DKB Confluence", RGB("#158dea"), {
        URL.parse("https://confluence.dkb.ag/dosearchsite.action?queryString=$it")
    }, "file", "alternate"),

    /**
     * [DKB Jira](https://jira.dkb.ag)
     */
    DkbJira("DKB Jira", RGB("#158dea"), {
        URL.parse("https://jira.dkb.ag/secure/QuickSearch.jspa?searchString=$it")
    }, "clipboard"),
    ;

    /**
     * The [SearchEngine] preceeding this one.
     */
    val prev: SearchEngine get() = values()[(ordinal - 1) fmod values().size]

    /**
     * Returns the [SearchEngine] preceeding this one among the specified [engines].
     */
    fun prev(engines: List<SearchEngine>) = engines.prev { it == this }.first()

    /**
     * The [SearchEngine] following this one.
     */
    val next: SearchEngine get() = values()[(ordinal + 1) fmod values().size]

    /**
     * The [SearchEngine] following this one among the specified [engines].
     */
    fun next(engines: List<SearchEngine>) = engines.next { it == this }.first()

    companion object {
        val Default: SearchEngine = Google
    }
}

/**
 * Returns a list that contains one previous element for each element the specified [of] returns `true`.
 */
fun <T> Iterable<T>.prev(of: (T) -> Boolean): List<T> = (this + this).windowed(2).mapNotNull { (current, next) -> current.takeIf { of(next) } }

/**
 * Returns a list that contains one next element for each element the specified [of] returns `true`.
 */
fun <T> Iterable<T>.next(of: (T) -> Boolean): List<T> = (this + this).windowed(2).mapNotNull { (current, next) -> next.takeIf { of(current) } }
