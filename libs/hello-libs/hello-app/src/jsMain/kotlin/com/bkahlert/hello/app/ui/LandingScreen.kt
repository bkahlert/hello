package com.bkahlert.hello.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.demo.DEMO_BASE_DELAY
import com.bkahlert.semanticui.element.Image
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.basic
import com.bkahlert.semanticui.element.centered
import com.bkahlert.semanticui.element.size
import kotlinx.coroutines.delay

@Composable
public fun LandingScreen(
    onTimeout: () -> Unit,
) {
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    LaunchedEffect(Unit) {
        delay(DEMO_BASE_DELAY)
        currentOnTimeout()
    }
    Segment({ v.basic() }) {
        Image(HelloImageFixtures.HelloFavicon, attrs = { v.size(Small).centered() })
    }
}
