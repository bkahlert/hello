package com.bkahlert.hello.ui

import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.clickup.ui.Pomodoro
import com.bkahlert.kommons.text.truncateEnd
import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement

//@JsModule("./pomodoro-completed.mp3")
//@JsNonModule
//private external val pomodoroCompleted: String

/** An acoustic signal */
fun interface SoundEffect {
    /** Plays this sound effect. */
    fun play()
}

/** A sound effect with no effect. */
object NoSoundEffect : SoundEffect {
    override fun play(): Unit = Unit
}

/** Sound effect that plays the audio encoded in the specified [data] URI. */
class DataSoundEffect(
    /** Base64 encoded data URI */
    val data: String,
) : SoundEffect {
    private val logger = simpleLogger()
    override fun play() {
        kotlin.runCatching {
            logger.debug("Playing sound effect ${data.truncateEnd(50)}")
            val parentElement = checkNotNull(document.body)
            val audioElement = document.createElement("audio") as HTMLAudioElement
            audioElement.apply {
                src = data
                controls = false
                style.visibility = "hidden"
                onended = { parentElement.removeChild(audioElement) }
            }
            parentElement.appendChild(audioElement)
            audioElement.play()
        }.onFailure { logger.warn("Failed to play sound effect", it) }
    }
}

/** Semantic collection of sound effect. */
interface AcousticFeedback {
    /** A sound effect to be provided if something completed. */
    val completed: SoundEffect

    companion object {
        /** Suppressed acoustic feedback. */
        val NoFeedback: AcousticFeedback = object : AcousticFeedback {
            override val completed: SoundEffect = NoSoundEffect
        }

        /** Acoustic feedback for [Pomodoro] tasks. */
        val PomodoroFeedback: AcousticFeedback = object : AcousticFeedback {
            override val completed: SoundEffect = NoSoundEffect
//            override val completed: SoundEffect = DataSoundEffect(pomodoroCompleted)
        }
    }
}
