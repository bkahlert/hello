package com.bkahlert.hello.fritz2.app.props

import kotlinx.serialization.json.JsonElement

/**
 * A data source for properties.
 */
public interface PropsDataSource {
    /**
     * Returns all properties.
     */
    public suspend fun get(): Map<String, JsonElement>

    /**
     * Sets the specified [props] by updating those that have changed and removing those that aren't present in [props].
     *
     * The difference is computed by comparing the [props] to the optional [cachedProps] (default: [get]).
     */
    public suspend fun set(props: Map<String, JsonElement>, cachedProps: Map<String, JsonElement>? = null): Map<String, JsonElement> {
        val currentProps = cachedProps ?: get()
        val updatedProps = setAll(props.filter { (key, value) -> value != currentProps[key] })
        val removedProps = removeAll(currentProps.keys.minus(props.keys))
        return currentProps + updatedProps - removedProps.keys
    }

    /**
     * Returns the property with the specified [id].
     */
    public suspend fun get(id: String): JsonElement?

    /**
     * Sets the property with the specified [id] to the specified [value].
     */
    public suspend fun set(id: String, value: JsonElement): JsonElement

    /**
     * Sets the specified [props].
     * Properties that aren't present in [props] aren't affected.
     */
    public suspend fun setAll(props: Map<String, JsonElement>): Map<String, JsonElement> = props.mapValues { (key, value) -> set(key, value) }

    /**
     * Removes the property with the specified [id].
     */
    public suspend fun remove(id: String): JsonElement?

    /**
     * Removes the properties with the specified [ids].
     */
    public suspend fun removeAll(ids: Iterable<String>): Map<String, JsonElement?> = ids.associateWith { id -> remove(id) }
}

/**
 * Convenience method for [setAll].
 */
public suspend fun PropsDataSource.setAll(vararg props: Pair<String, JsonElement>): Map<String, JsonElement> = setAll(props.toMap())

/**
 * Convenience method for [removeAll].
 */
public suspend fun PropsDataSource.removeAll(vararg ids: String): Map<String, JsonElement?> = removeAll(ids.asList())
