package com.bkahlert.hello.clickup.viewmodel.fixtures

import com.bkahlert.hello.clickup.model.fixtures.ClickUpTestClient
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.CoreData
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected.Data.FullData
import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class ClickUpMenuStatesKtTest {

    @Test
    fun to_team_selecting() = testAll {
        testClient.toTeamSelecting() should {
            it.user shouldBe testClient.initialUser
            it.teams shouldBe testClient.initialTeams
        }
    }

    @Test
    fun to_partially_loaded() = testAll {
        testClient.toPartiallyLoaded() should {
            it.user shouldBe testClient.initialUser
            it.teams shouldBe testClient.initialTeams
            it.selectedTeam shouldBe testClient.initialTeams.first()
            it.selected.shouldNotBeEmpty()
            it.data.shouldBeInstanceOf<CoreData>()
        }
    }

    @Test
    fun to_fully_loaded() = testAll {
        testClient.toFullyLoaded() should {
            it.user shouldBe testClient.initialUser
            it.teams shouldBe testClient.initialTeams
            it.selectedTeam shouldBe testClient.initialTeams.first()
            it.selected.shouldNotBeEmpty()
            it.data.shouldBeInstanceOf<FullData>()
        }
    }
}

val testClient = ClickUpTestClient()
