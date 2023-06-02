package com.bkahlert.hello.fritz2

import dev.fritz2.core.WithJob
import kotlinx.browser.document
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.promise
import org.w3c.dom.Element

public fun <T> runTest(block: suspend WithJob.() -> T): dynamic = MainScope().promise {
    delay(50)
    block(object : WithJob {
        override val job: Job = Job()
    })
    delay(50)
}


public inline fun <reified E : Element> getElementById(id: String): E = document.getElementById(id) as E
