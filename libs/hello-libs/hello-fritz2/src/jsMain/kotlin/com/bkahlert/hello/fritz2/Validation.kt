package com.bkahlert.hello.fritz2

import com.bkahlert.kommons.json.LenientAndPrettyJson
import dev.fritz2.core.Inspector
import dev.fritz2.core.Lens
import dev.fritz2.core.Store
import dev.fritz2.core.Tag
import dev.fritz2.core.mountSimple
import dev.fritz2.validation.ValidatingStore
import dev.fritz2.validation.Validation
import dev.fritz2.validation.ValidationMessage
import dev.fritz2.validation.validation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLFieldSetElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLKeygenElement
import org.w3c.dom.HTMLObjectElement
import org.w3c.dom.HTMLOutputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement


/**
 * Creates a new [ValidatingStore] that contains data derived by a given [lens],
 * and [ValidatingStore.messages] encapsulating exceptions thrown by [Lens.set].
 */
public fun <D, X> Store<D>.mapValidating(
    lens: Lens<D, X>,
): ValidatingStore<X, Unit, ValidationMessage> = object : ValidatingStore<X, Unit, ValidationMessage>(
    initialData = lens.get(current),
    validation = validateCatching { lens.set(current, it.data).also { validated -> update(validated) } }
) {
    init {
        validate(current)
    }
}

/** A flow of error messages with each error message being the concatenation of all errors. */
public val ValidatingStore<*, *, out ValidationMessage>.errorMessage: Flow<String>
    get() = messages.map { messages ->
        messages.filter { it.isError }.joinToString { it.toString() }
    }

/** Creates a [ValidationMessage] from this exception. */
public fun Throwable.toValidationMessage(path: String = ""): ValidationMessage =
    object : ValidationMessage {
        override val path: String = path
        override val isError: Boolean = true
        override fun toString(): String = message ?: this@toValidationMessage.toString()
    }

/** Creates a [Validation] creates [ValidationMessage] instances from thrown exceptions. */
public fun <D> validateCatching(block: (Inspector<D>) -> Unit): Validation<D, Unit, ValidationMessage> =
    validation { inspector ->
        kotlin
            .runCatching { block(inspector) }
            .onFailure { add(it.toValidationMessage()) }
    }

public fun <T> validateSerialization(stringFormat: StringFormat, deserializer: DeserializationStrategy<T>): Validation<String, Unit, ValidationMessage> =
    validateCatching { inspector ->
        stringFormat.decodeFromString(deserializer, inspector.data)
    }

public inline fun <reified T> validateSerialization(stringFormat: StringFormat): Validation<String, Unit, ValidationMessage> =
    validateSerialization(stringFormat, serializer<T>())

public fun validateJson(json: Json = LenientAndPrettyJson): Validation<String, Unit, ValidationMessage> =
    validateSerialization(json, serializer<JsonElement>())


/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLObjectElement>.customValidity(message: String): Unit = domNode.setCustomValidity(message)

/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLObjectElement>.customValidity(message: Flow<String>): Unit = mountSimple(job, message, ::customValidity)


/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLInputElement>.customValidity(message: String): Unit = domNode.setCustomValidity(message)

/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLInputElement>.customValidity(message: Flow<String>): Unit = mountSimple(job, message, ::customValidity)


/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLButtonElement>.customValidity(message: String): Unit = domNode.setCustomValidity(message)

/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLButtonElement>.customValidity(message: Flow<String>): Unit = mountSimple(job, message, ::customValidity)


/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLSelectElement>.customValidity(message: String): Unit = domNode.setCustomValidity(message)

/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLSelectElement>.customValidity(message: Flow<String>): Unit = mountSimple(job, message, ::customValidity)


/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLTextAreaElement>.customValidity(message: String): Unit = domNode.setCustomValidity(message)

/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLTextAreaElement>.customValidity(message: Flow<String>): Unit = mountSimple(job, message, ::customValidity)


/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLKeygenElement>.customValidity(message: String): Unit = domNode.setCustomValidity(message)

/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLKeygenElement>.customValidity(message: Flow<String>): Unit = mountSimple(job, message, ::customValidity)


/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLOutputElement>.customValidity(message: String): Unit = domNode.setCustomValidity(message)

/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLOutputElement>.customValidity(message: Flow<String>): Unit = mountSimple(job, message, ::customValidity)


/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLFieldSetElement>.customValidity(message: String): Unit = domNode.setCustomValidity(message)

/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation). */
public fun Tag<HTMLFieldSetElement>.customValidity(message: Flow<String>): Unit = mountSimple(job, message, ::customValidity)
