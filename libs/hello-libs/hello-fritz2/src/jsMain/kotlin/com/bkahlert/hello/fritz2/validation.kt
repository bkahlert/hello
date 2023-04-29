package com.bkahlert.hello.fritz2

import com.bkahlert.kommons.js.ConsoleLogger
import dev.fritz2.core.Tag
import dev.fritz2.headless.components.CheckboxGroup
import dev.fritz2.headless.components.InputField
import dev.fritz2.headless.components.Listbox
import dev.fritz2.headless.components.SwitchWithLabel
import dev.fritz2.headless.components.TextArea
import dev.fritz2.headless.foundation.ValidationMessages
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFieldSetElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLKeygenElement
import org.w3c.dom.HTMLObjectElement
import org.w3c.dom.HTMLOutputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement

private val logger = ConsoleLogger("hello.validation")

/** Converts Fritz2 [ValidationMessages] to native validation messages. */
public fun CheckboxGroup<*, *>.mergeValidationMessages(input: Tag<HTMLButtonElement>) {
    checkboxGroupValidationMessages("hidden", initialize = mergeValidationMessagesInit(input))
}

/** Converts Fritz2 [ValidationMessages] to native validation messages. */
public fun InputField<*>.mergeValidationMessages(input: Tag<HTMLInputElement>) {
    inputValidationMessages("hidden", initialize = mergeValidationMessagesInit(input))
}

/** Converts Fritz2 [ValidationMessages] to native validation messages. */
public fun Listbox<*, *>.mergeValidationMessages(input: Tag<HTMLButtonElement>) {
    listboxValidationMessages("hidden", initialize = mergeValidationMessagesInit(input))
}

/** Converts Fritz2 [ValidationMessages] to native validation messages. */
public fun SwitchWithLabel<*>.mergeValidationMessages(input: Tag<HTMLButtonElement>) {
    switchValidationMessages("hidden", initialize = mergeValidationMessagesInit(input))
}

/** Converts Fritz2 [ValidationMessages] to native validation messages. */
public fun TextArea<*>.mergeValidationMessages(input: Tag<HTMLTextAreaElement>) {
    textareaValidationMessages("hidden", initialize = mergeValidationMessagesInit(input))
}

private fun mergeValidationMessagesInit(input: Tag<HTMLElement>): ValidationMessages<HTMLDivElement>.() -> Unit = {
    msgs.map { messages -> messages.filter { it.isError } } handledBy { errors ->
        input.customValidityAndReport(errors.joinToString("\n") { it.message })
    }
}

/** Sets the [customValidity](https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation) and reports it. */
private fun Tag<HTMLElement>.customValidityAndReport(message: String): Unit {
    // @formatter:off
    when(val node = domNode) {
        is HTMLObjectElement -> { node.setCustomValidity(message); node.reportValidity() }
        is HTMLInputElement -> { node.setCustomValidity(message); node.reportValidity() }
        is HTMLButtonElement -> { node.setCustomValidity(message); node.reportValidity() }
        is HTMLSelectElement -> { node.setCustomValidity(message); node.reportValidity() }
        is HTMLTextAreaElement -> { node.setCustomValidity(message); node.reportValidity() }
        is HTMLKeygenElement -> { node.setCustomValidity(message); node.reportValidity() }
        is HTMLOutputElement -> { node.setCustomValidity(message); node.reportValidity() }
        is HTMLFieldSetElement -> { node.setCustomValidity(message); node.reportValidity() }
        else -> logger.warn("setCustomValidity not supported by element",node)
    }
    // @formatter:on
}
