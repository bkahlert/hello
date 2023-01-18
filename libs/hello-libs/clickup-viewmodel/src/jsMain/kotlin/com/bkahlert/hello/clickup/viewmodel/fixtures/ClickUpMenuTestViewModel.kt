package com.bkahlert.hello.clickup.viewmodel.fixtures

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.hello.clickup.model.fixtures.ClickUpTestClient
import com.bkahlert.hello.clickup.view.ClickUpTestClientConfigurer
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuViewModel
import com.bkahlert.hello.clickup.viewmodel.rememberClickUpMenuViewModel
import com.bkahlert.kommons.dom.InMemoryStorage
import com.bkahlert.kommons.dom.Storage
import kotlinx.coroutines.CoroutineScope

/**
 * Returns a remembered [ClickUpMenuViewModel] for the purpose of testing with the
 * optionally specified [refreshCoroutineScope], [storage]
 * and the [init] derived from the optionally specified [testClient].
 */
@Composable
public fun rememberClickUpMenuTestViewModel(
    testClient: ClickUpTestClient = ClickUpTestClient(),
    refreshCoroutineScope: CoroutineScope = rememberCoroutineScope(),
    storage: Storage = InMemoryStorage(),
    init: ClickUpTestClient.() -> ClickUpMenuState,
): ClickUpMenuViewModel {
    val initialState = testClient.init()
    return rememberClickUpMenuViewModel(
        initialState = initialState,
        refreshCoroutineScope = refreshCoroutineScope,
        storage = storage,
        configurers = arrayOf(ClickUpTestClientConfigurer(
            name = "Demo (${init::class.simpleName})",
            provide = { testClient }
        )),
    )
}
