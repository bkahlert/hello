package com.bkahlert.hello.data

/** The state of an attempt to load data of type [T]. */
public sealed class DataRetrieval<out T> {

    /** An attempt to load data is in progress. */
    public object Ongoing : DataRetrieval<Nothing>()

    /** Successfully loaded [data]. */
    public data class Succeeded<out T>(
        /** The loaded data. */
        public val data: T,
    ) : DataRetrieval<T>()

    /** A failed attempt to load data. */
    public data class Failed(
        /** The cause of this failed state. */
        val message: String,
        /** The cause of this failed state. */
        val cause: Throwable,
    ) : DataRetrieval<Nothing>()
}
