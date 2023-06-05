package com.bkahlert.hello.chatbot

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.kommons.uri.GravatarImageUri
import com.bkahlert.kommons.uri.Uri

public val ChatMessage.icon: Uri
    get() = when (role) {
        ChatRole.Assistant -> Chatbot.ChatGPT.logo
        ChatRole.User -> name?.let { GravatarImageUri(it, size = 256) } ?: SolidHeroIcons.user_circle
        ChatRole.System -> SolidHeroIcons.cog
        else -> SolidHeroIcons.question_mark_circle
    }
