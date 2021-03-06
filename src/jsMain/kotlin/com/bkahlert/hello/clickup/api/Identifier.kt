package com.bkahlert.hello.clickup.api

import com.bkahlert.kommons.dom.div
import io.ktor.http.Url
import kotlin.reflect.KClass

sealed interface Identifier<T> {
    val id: T
    val stringValue: String get() = id as? String ?: id.toString()
    val typedStringValue: String get() = "${this::class.simpleName}$Separator$stringValue"

    companion object {
        private const val Separator: String = "::"
        private val KClass<*>.lowerCaseSimpleName get() = simpleName?.lowercase()

        /**
         * Returns the correctly typed [Identifier] representing the specified [typedStringValue].
         */
        fun of(typedStringValue: String): Identifier<*> {
            val identifier: Identifier<*> = typedStringValue.split("::", limit = 2)
                .also { check(it.size == 2 && it.all { it.isNotEmpty() }) { "unexpected identifier $typedStringValue; expected format: <Type>::<ID>" } }
                .let { (type, stringValue) ->
                    when (type.lowercase()) {
                        CheckListID::class.lowerCaseSimpleName -> CheckListID(stringValue)
                        CustomFieldID::class.lowerCaseSimpleName -> CustomFieldID(stringValue)
                        FolderID::class.lowerCaseSimpleName -> FolderID(stringValue)
                        SpaceID::class.lowerCaseSimpleName -> SpaceID(stringValue)
                        StatusID::class.lowerCaseSimpleName -> StatusID(stringValue)
                        TaskID::class.lowerCaseSimpleName -> TaskID(stringValue)
                        TaskLinkID::class.lowerCaseSimpleName -> TaskLinkID(stringValue)
                        TaskListID::class.lowerCaseSimpleName -> TaskListID(stringValue)
                        TeamID::class.lowerCaseSimpleName -> TeamID(stringValue)
                        TimeEntryID::class.lowerCaseSimpleName -> TimeEntryID(stringValue)
                        UserID::class.lowerCaseSimpleName -> UserID(stringValue.toInt())
                        else -> throw IllegalStateException("illegal type $type")
                    }
                }
            // if this "when" turns red, add missing class AND add the deserialization above
            return when (identifier) {
                is CheckListID,
                is CustomFieldID,
                is FolderID,
                is SpaceID,
                is StatusID,
                is TaskID,
                is TaskLinkID,
                is TaskListID,
                is TeamID,
                is TimeEntryID,
                is UserID,
                -> identifier
            }
        }
    }
}

infix operator fun <T> Url.div(path: Identifier<T>): Url = div(path.id.toString())
