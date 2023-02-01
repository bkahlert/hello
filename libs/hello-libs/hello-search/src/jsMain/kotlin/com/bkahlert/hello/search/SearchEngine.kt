package com.bkahlert.hello.search

import com.bkahlert.hello.search.SearchEngine.Google
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.net.Uri
import com.bkahlert.kommons.util.predecessor
import com.bkahlert.kommons.util.successor

/**
 * Search engines, such as [Google].
 */
@Suppress("SpellCheckingInspection")
public enum class SearchEngine(
    /**
     * Name of the search engine.
     */
    private val caption: String,
    /**
     * Brand color of the search engine.
     */
    public val color: Color,
    /**
     * URL creator for a given query.
     */
    public val url: (String) -> Uri,
    /**
     * Name(s) of the icon [Semantic UI icon](https://semantic-ui.com/elements/icon.html).
     */
    public vararg val icon: String,
) {
    /**
     * [Linguee](https://www.linguee.de)
     */
    Linguee("Linguee", RGB(0x0e2b46), {
        Uri.parse("https://www.linguee.de/deutsch-englisch/search?source=auto&query=$it")
    }, "language"),

    /**
     * [Google](https://www.google.de)
     */
    Google("Google", RGB("#EC4A3A"), {
        Uri.parse("https://www.google.de/search?q=$it")
    }, "google"),

    /**
     * [start.me](https://start.me)
     */
    StartMe("start.me", RGB("#4cb4e0"), { // #0093D4
        Uri.parse("https://start.me/search/google?q=$it")
    }, "bookmark"),

    /**
     * [Bing](https://www.bing.com)
     */
    Bing("Bing", RGB("#02809D"), {
        Uri.parse("https://www.bing.com/search?q=$it")
    }, "microsoft"),

    /**
     * [Yahoo](https://de.search.yahoo)
     */
    Yahoo("Yahoo", RGB("#542DFE"), {
        Uri.parse("https://de.search.yahoo.com/yhs/search?p=$it")
    }, "yahoo"),

    /**
     * [Yandex Video](https://yandex.com/video)
     */
    YandexVideo("Yandex Video", RGB("#FF0202"), {
        Uri.parse("https://yandex.com/video/search?text=$it)")
    }, "yandex"),

    /**
     * [Google Dataset Search](https://datasetsearch.research.google)
     */
    GoogleDataset("Google Dataset Search", RGB("#5F6367"), {
        Uri.parse("https://datasetsearch.research.google.com/search?query=$it")
    }, "database"),

    /**
     * [Qwant](https://www.qwant.com)
     */
    Qwant("Qwant", RGB("#AF27CC"), {
        Uri.parse("https://www.qwant.com/?q=$it")
    }, "eye", "slash"),

    /**
     * [Duck Duck Go](https://duckduckgo.com/?q)
     */
    @Suppress("GrazieInspection")
    DuckDuckGo("Duck Duck Go", RGB("#65BC46"), {
        Uri.parse("https://duckduckgo.com/?q=$it")
    }, "eye", "slash"),

    /**
     * [Glyph Search](https://glyphsearch.com/?query)
     */
    GlyphSearch("glyphsearch", RGB("#2b3e51"), {
        Uri.parse("https://glyphsearch.com/?query=$it")
    }, "images"),

    /**
     * [flaticon](https://www.flaticon.com)
     */
    FlatIcon("flaticon", RGB("#49d295"), {
        Uri.parse("https://www.flaticon.com/search?license=selection&order_by=4&grid=small&word=$it")
    }, "images"),

    /**
     * [freepik](https://de.freepik.com)
     */
    FreePik("freepik", RGB("#0e60c8"), {
        Uri.parse("https://de.freepik.com/search?dates=any&format=search&page=1&selection=1&sort=popular&query=$it")
    }, "images"),

    /**
     * [thenounproject](https://thenounproject.com/search)
     */
    TheNounProject("thenounproject", RGB("#000000"), {
        Uri.parse("https://thenounproject.com/search/?q=$it")
    }, "images"),

    /**
     * [iconfinder](https://www.iconfinder.com)
     */
    IconFinder("iconfinder", RGB("#1a916c"), {
        Uri.parse("https://www.iconfinder.com/search/?price=free&q=$it")
    }, "images"),

    /**
     * [iconmonstr](https://iconmonstr.com/?s)
     */
    IconMonstr("iconmonstr", RGB("#000000"), {
        Uri.parse("https://iconmonstr.com/?s=$it")
    }, "images"),

    /**
     * [icons8](https://icons8.com/icons)
     */
    Icons8("icons8", RGB("#28b351"), {
        Uri.parse("https://icons8.com/icons/set/$it")
    }, "images"),

    /**
     * [dryicons](https://dryicons.com/free)
     */
    DryIcons("dryicons", RGB("#eb4239"), {
        Uri.parse("https://dryicons.com/free-icons/$it")
    }, "images"),

    /**
     * [vecteezy](https://www.vecteezy.com)
     */
    VectEezy("vecteezy", RGB("#f58400"), {
        Uri.parse("https://www.vecteezy.com/free-vector/$it?license-free=true")
    }, "images"),

    /**
     * [iconspedia](https://www.iconspedia.com)
     */
    IconsPedia("iconspedia", RGB("#00bedb"), {
        Uri.parse("https://www.iconspedia.com/search/$it/")
    }, "images"),

    /**
     * [fontello](http://fontello.com/#search)
     */
    Fontello("fontello", RGB("#c9312c"), {
        Uri.parse("http://fontello.com/#search=$it")
    }, "images"),

    /**
     * [iconarchive](http://www.iconarchive.com)
     */
    IconArchive("iconarchive", RGB("#5c7ab8"), {
        Uri.parse("http://www.iconarchive.com/search?q=$it")
    }, "images"),

    /**
     * [unsplash](https://unsplash.com/s)
     */
    Unsplash("unsplash", RGB("#111111"), {
        Uri.parse("https://unsplash.com/s/photos/$it")
    }, "images"),

    /**
     * [DKB Confluence](https://confluence.dkb.ag)
     */
    DkbConfluence("DKB Confluence", RGB("#158dea"), {
        Uri.parse("https://confluence.dkb.ag/dosearchsite.action?queryString=$it")
    }, "file", "alternate"),

    /**
     * [DKB Jira](https://jira.dkb.ag)
     */
    DkbJira("DKB Jira", RGB("#158dea"), {
        Uri.parse("https://jira.dkb.ag/secure/QuickSearch.jspa?searchString=$it")
    }, "clipboard"),
    ;

    /**
     * The [SearchEngine] preceeding this one.
     */
    public val prev: SearchEngine get() = predecessor

    /**
     * Returns the [SearchEngine] preceeding this one among the specified [engines].
     */
    public fun prev(engines: List<SearchEngine>): SearchEngine = engines.predecessor { it == this }.first()

    /**
     * The [SearchEngine] following this one.
     */
    public val next: SearchEngine get() = successor

    /**
     * The [SearchEngine] following this one among the specified [engines].
     */
    public fun next(engines: List<SearchEngine>): SearchEngine = engines.successor { it == this }.first()

    public companion object {
        public val Default: SearchEngine = Google
    }
}
