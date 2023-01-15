package com.bkahlert.hello.dom

import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement

//@JsModule("./pomodoro-completed.mp3")
//@JsNonModule
//private external val pomodoroCompleted: String

/** An acoustic signal */
public fun interface SoundEffect {
    /** Plays this sound effect. */
    public fun play()
}

/** A sound effect with no effect. */
public object NoSoundEffect : SoundEffect {
    override fun play(): Unit = Unit
}

/** Sound effect that plays the audio encoded in the specified [data] URI. */
public class DataSoundEffect(
    /** Base64 encoded data URI */
    public val data: String,
) : SoundEffect {
    override fun play() {
        kotlin.runCatching {
            console.log("Playing sound effect ${data.substring(0, 50)}")
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
        }.onFailure { console.warn("Failed to play sound effect", it) }
    }
}

/** Semantic collection of sound effect. */
public interface AcousticFeedback {
    /** A sound effect to be provided if something completed. */
    public val completed: SoundEffect

    public companion object {
        /** Suppressed acoustic feedback. */
        public val NoFeedback: AcousticFeedback = object : AcousticFeedback {
            override val completed: SoundEffect = NoSoundEffect
        }

        /** Acoustic feedback for Pomodoro tasks. */
        public val PomodoroFeedback: AcousticFeedback = object : AcousticFeedback {
            override val completed: SoundEffect = NoSoundEffect
//            override val completed: SoundEffect = DataSoundEffect(pomodoroCompleted)
        }
    }
}
