package com.bkahlert.kommons.dom

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

public inline fun <reified T : EventTarget> Flow<Event>.mapTarget(): Flow<T> = mapNotNull { it.target as? T }
