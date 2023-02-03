package com.bkahlert.hello.clickup.model

import com.bkahlert.kommons.uri.div
import io.ktor.http.Url
import kotlin.reflect.KClass

public sealed interface Identifier<T> {
    public val id: T
    public val stringValue: String get() = id as? String ?: id.toString()
    public val typedStringValue: String get() = "${this::class.simpleName}$Separator$stringValue"

    public companion object {
        private const val Separator: String = "::"
        private val KClass<*>.lowerCaseSimpleName get() = simpleName?.lowercase()

        /**
         * Returns the correctly typed [Identifier] representing the specified [typedStringValue].
         */
        public fun of(typedStringValue: String): Identifier<*> {
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

public infix operator fun <T> Url.div(path: Identifier<T>): Url = div(path.stringValue)
