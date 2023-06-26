package com.bkahlert.hello.app.widgets

import com.bkahlert.hello.bookmark.BookmarksWidget
import com.bkahlert.hello.chatbot.ChatbotWidget
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.widget.WidgetRegistration
import com.bkahlert.hello.widget.image.ImageWidget
import com.bkahlert.hello.widget.preview.FeaturePreviewWidget
import com.bkahlert.hello.widget.ssh.WsSshWidget
import com.bkahlert.hello.widget.video.VideoWidget
import com.bkahlert.hello.widget.website.WebsiteWidget

public val DefaultWidgetRegistration: WidgetRegistration by lazy {
    WidgetRegistration().apply {
        register<BookmarksWidget>(
            "bookmarks",
            title = "Bookmarks",
            description = "Manage, import and export bookmarks",
            icon = SolidHeroIcons.bookmark_square,
        )
        register<ChatbotWidget>(
            "chatbot",
            title = "Chatbot",
            description = "OpenAI's GPT chatbot",
            icon = SolidHeroIcons.chat_bubble_left_right
        )
        register<FeaturePreviewWidget>(
            "feature-preview",
            title = "Feature Preview",
            description = "Demo of a future feature",
            icon = SolidHeroIcons.star
        )
        register<ImageWidget>(
            "image",
            title = "Image",
            description = "Displays an image",
            icon = SolidHeroIcons.photo
        )
        register<VideoWidget>(
            "video",
            title = "Video",
            description = "Embeds a video",
            icon = SolidHeroIcons.video_camera
        )
        register<WebsiteWidget>(
            "website", "embed",
            title = "Website",
            description = "Embeds an external website",
            icon = SolidHeroIcons.window
        )
        register<WsSshWidget>(
            "ws-ssh",
            title = "SSH",
            description = """Connect to a SSH server via a <a href="https://github.com/bkahlert/ws-ssh">WS-SSH proxy</a>.""",
            icon = SolidHeroIcons.command_line,
        )
    }
}
