package com.bkahlert.kommons.js

@JsModule("debug")
internal external fun debug(namespace: String): dynamic

internal interface Debugger {
    operator fun invoke(vararg args: Any?)

    /**
     * @see <a href="https://www.npmjs.com/package/debug#output-streams">Output streams</a>
     */
    var log: dynamic

    /**
     * @see <a href="https://www.npmjs.com/package/debug#user-content-custom-formatters">Custom formatters</a>
     */
    val formatters: Formatters
}

internal interface Formatters {
    operator fun get(key: String): (Any?) -> String
    operator fun set(key: String, formatter: (Any?) -> String)
}
