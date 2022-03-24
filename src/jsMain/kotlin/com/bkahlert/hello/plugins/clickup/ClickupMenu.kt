package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bkahlert.hello.AppConfig
import com.bkahlert.hello.plugins.clickup.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Disconnected
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Failed
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Initializing
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.TeamSelected
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.TeamSelecting
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loading
import com.bkahlert.hello.ui.AcousticFeedback
import com.bkahlert.hello.ui.ErrorMessage
import com.bkahlert.hello.ui.textOverflow
import com.bkahlert.kommons.asAvatar
import com.bkahlert.kommons.backgroundImage
import com.bkahlert.kommons.coroutines.flow.toStringAndHash
import com.bkahlert.kommons.dom.InMemoryStorage
import com.clickup.api.Tag
import com.clickup.api.TaskID
import com.clickup.api.Team
import com.clickup.api.TeamID
import com.clickup.api.TimeEntry
import com.clickup.api.User
import com.clickup.api.rest.AccessToken
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.State
import com.semanticui.compose.Variation
import com.semanticui.compose.Variation.Colored.Red
import com.semanticui.compose.Variation.Columns.One
import com.semanticui.compose.Variation.Position.Bottom
import com.semanticui.compose.Variation.Position.Right
import com.semanticui.compose.Variation.Size.Mini
import com.semanticui.compose.collection.DropdownItem
import com.semanticui.compose.collection.LinkItem
import com.semanticui.compose.collection.Menu
import com.semanticui.compose.collection.MenuElement
import com.semanticui.compose.collection.SubMenu
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconGroup
import com.semanticui.compose.view.Item
import com.semanticui.compose.view.ItemElement
import kotlinx.browser.window
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.opacity
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.style
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun InitializingClickupMenu() {
    Div({
        classes("ui", "placeholder", "fluid")
        style { height(33.4.px) }
    }) {
        Div({ classes("line") })
    }
}

@Composable
fun DisconnectedClickupMenu(
    onConnect: (details: (defaultAccessToken: AccessToken?, callback: (AccessToken) -> Unit) -> Unit) -> Unit = {},
) {
    Menu({ +Size.Mini }) {
        DropdownItem(Unit, { _, _, _ -> }, { variation(Borderless, Variation.Icon) }) {
            Icon("youtube")
            SubMenu {
                Item({
                    +Link
                    onClick {
                        onConnect { defaultAccessToken, callback ->
                            // TODO https://semantic-ui.com/modules/modal.html
                            val accessToken = window.prompt("""
                                    Currently, OAuth2 is not supported yet.
                                    
                                    To use this feature at its current state,
                                    please enter your personal ClickUp API token.
                                    
                                    More information can be found on https://clickup.com/api
                                    """.trimIndent(), defaultAccessToken?.token ?: "")
                            if (accessToken != null) callback(AccessToken(accessToken))
                        }
                        it.preventDefault()
                    }
                }) {
                    Icon("sign-in")
                    Text("Sign-in")
                }
            }
        }
        DropdownItem(Unit, { _, _, _ -> }, {
            variation(Borderless, Variation.Icon)
            state(State.Disabled)
        }) {
            Icon("stopwatch")
        }
    }
}

@Composable
fun LoadingClickupMenu() {
    Menu({
        variation(Mini, Fluid, One)
        classes("item")
    }) {
        DropdownItem(Unit, { _, _, _ -> }, {
            variation(Borderless, Variation.Icon)
            state(State.Loading, State.Disabled)
        }) {
            Icon("youtube")
        }
    }
}

@Composable
fun SemanticElementScope<MenuElement, *>.ClickupMenuTeamSelectingItems(
    state: TeamSelecting,
    onActivate: (TeamID) -> Unit = {},
) {
    DropdownItem(state, { _, _, _ -> }, {
        variation(Borderless, Variation.Icon)
    }) {
        Icon("youtube")
        SubMenu {
            Item {
                Icon("sign-out")
                Text("Sign-out")
            }
        }
    }
    state.teams.forEach { (id, name, color, avatar, _) ->
        LinkItem({
            variation(Borderless)
            onClick { onActivate(id) }
        }) {
            Img(src = avatar.toString()) {
                classes("ui", "avatar", "image")
            }
            Span { Text(name) }
            Icon("grid", "layout", {
                style { color(color) }
            })
            Text(name)
        }
    }
}

@Composable
fun SemanticElementScope<MenuElement, *>.ClickupMenuMainItems(
    user: User,
    teams: List<Team>,
    selectedTeam: Team?,
    onTeamSelect: (TeamID) -> Unit = {},
    onRefresh: () -> Unit = { },
) {

    DropdownItem(Unit, { _, _, _ -> }, {
        +Borderless + Icon
        style {
            backgroundImage(user.profilePicture.asAvatar())
            backgroundSize("2.5em")
            backgroundRepeat("no-repeat")
            backgroundPosition("center")
        }
    }) {

        if (selectedTeam != null) {
            Img(src = user.profilePicture.toString()) {
                classes("ui", "avatar", "image")
                style { opacity(0) }
            }
        } else {
            Icon("youtube")
        }
        SubMenu {
            TeamSelectionItems(teams, selectedTeam, onTeamSelect)
            LinkItem({
                onClick { onRefresh() }
            }) {
                Icon("sync")
                Text("Refresh")
            }
            LinkItem({
                onClick {
                    console.warn("Sign out not implemented but clicked")
                }
            }) {
                Icon("sign-out")
                Text("Sign-out")
            }
        }
    }
}

@Composable
fun SemanticElementScope<MenuElement, *>.TeamSelectionItems(
    teams: List<Team>,
    selectedTeam: Team?,
    onTeamSelect: (TeamID) -> Unit,
) {
    when (teams.size) {
        0 -> {
            Item {
                Icon("dropdown")
                Span({ classes("text") }) { Text("Switch Team") }
            }
        }
        1 -> {
            val team = teams.first()
            TeamItem(
                team = team,
                onClick = { onTeamSelect(team.id) }
            ) {
                if (team.id == selectedTeam?.id) +Disabled + Active
            }
        }
        else -> Item {
            Icon("dropdown")
            Span({ classes("text") }) { Text("Switch Team") }
            SubMenu {
                teams.forEach { team ->
                    TeamItem(
                        team = team,
                        onClick = { onTeamSelect(team.id) }
                    ) {
                        if (team.id == selectedTeam?.id) +Disabled + Active
                    }
                }
            }
        }
    }
}

@Composable
fun SemanticElementScope<MenuElement, *>.TeamItem(
    team: Team,
    onClick: () -> Unit,
    attrs: SemanticAttrBuilder<ItemElement, HTMLDivElement>? = null,
) {
    LinkItem({
        attrs?.invoke(this)
        onClick { onClick() }
    }) {
        Div({
            classes("ui", "image")
            style {
                marginLeft((-5).px)
                marginRight(2.px)
            }
        }) {
            Img(src = team.avatar.toString()) {
                classes("ui", "avatar", "image")
            }
        }
        Span({ style { property("padding-right", "3em") } }) {
            Text(team.name)
        }
    }
}

@Composable
fun SemanticElementScope<MenuElement, *>.ClickupMenuActivityItems(
    activityGroups: List<ActivityGroup>,
    onSelect: (Selection) -> Unit = {},
    onTimeEntryStart: (TaskID, List<Tag>, billable: Boolean) -> Unit = { _, _, _ -> },
    onTimeEntryStop: (TimeEntry, List<Tag>) -> Unit = { _, _ -> },
) {
    val selectedActivity: Activity<*>? = activityGroups.selected.firstOrNull()

    LinkItem({
        variation(Borderless)
        if (selectedActivity == null) state(State.Disabled)
    }) {
        when (selectedActivity) {
            is RunningTaskActivity -> {
                PomodoroTimer(
                    timeEntry = selectedActivity.timeEntry,
                    onStop = onTimeEntryStop,
                    progressIndicating = false,
                    acousticFeedback = AcousticFeedback.PomodoroFeedback,
                )
            }
            else -> {
                PomodoroStarter(
                    taskID = selectedActivity?.taskID,
                    onStart = onTimeEntryStart,
                    acousticFeedback = AcousticFeedback.PomodoroFeedback,
                )
            }
        }
    }
    Item({
        variation(Borderless)
        if (selectedActivity == null) state(State.Disabled)
        style {
            flex(1, 1)
            minWidth("0") // https://css-tricks.com/flexbox-truncated-text/
        }
    }) {
        ActivityDropdown(activityGroups) { onSelect(it) }
    }

    SubMenu({ variation(Direction.Right) }) { MetaItems(selectedActivity?.meta?.reversed()) }
}

@Composable
fun SemanticElementScope<MenuElement, *>.ClickupMenuFailedItems(
    state: Failed,
) {
    DropdownItem(state, { _, _, _ -> }, {
        variation(Borderless, Floating, Variation.Icon)
    }) {
        IconGroup {
            Icon("youtube")
            Icon("exclamation", "circle", { variation(Red, Bottom, Right, Corner) })
        }
        SubMenu {
            ErrorMessage(state.exceptions.first())
            Item {
                Icon("sign-out")
                Text("Sign-out")
            }
        }
    }
}

@Composable
fun ClickupMenu(
    clickupModel: ClickupModel = remember { ClickupModel(AppConfig.clickup, InMemoryStorage()) },
) {
    val _state by clickupModel.menuState.collectAsState(Initializing)
    console.warn("STATE ${_state.toStringAndHash()}")
    when (val state = _state) {
        Initializing -> InitializingClickupMenu()
        Disconnected -> DisconnectedClickupMenu { details ->
            details(AppConfig.clickup.fallbackAccessToken, clickupModel::configureClickUp)
        }
        Loading -> LoadingClickupMenu()
        is TeamSelecting -> {
            Menu({ +Size.Mini }) {
                ClickupMenuTeamSelectingItems(
                    state = state,
                    onActivate = clickupModel::selectTeam,
                )
            }

            DisposableEffect(state.teams) {
                if (state.teams.size == 1) {
                    clickupModel.selectTeam(state.teams.first().id)
                }
                onDispose { }
            }
        }
        is TeamSelected -> {
            Menu({ +Size.Mini }) {
                ClickupMenuMainItems(
                    user = state.user,
                    teams = state.teams,
                    selectedTeam = state.selectedTeam,
                    onTeamSelect = clickupModel::selectTeam,
                    onRefresh = { clickupModel.refresh(force = true) },
                )
                ClickupMenuActivityItems(
                    activityGroups = state.activityGroups,
                    onSelect = clickupModel::select,
                    onTimeEntryStart = clickupModel::startTimeEntry,
                    onTimeEntryStop = { _, tags -> clickupModel.stopTimeEntry(tags) },
                )
            }

            DisposableEffect(state.selectedTeam) {
                clickupModel.refresh()
                onDispose { }
            }
        }
        is Failed -> {
            Menu({ +Size.Mini }) {
                ClickupMenuFailedItems(state)
            }
        }
    }
}


// TODO try to use instead of semantic tags
@Composable
fun Tag(
    tag: Tag,
    outline: Boolean = false,
) {
    Div({
        style {
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
            minWidth(41.px)
            height(20.px)
            borderRadius(2.px, 13.px, 13.px, 2.px)
            margin(3.px, 4.px, 4.px, 0.px)
            padding(0.px, 10.px, 0.px, 8.px)
            whiteSpace("nowrap")
            lineHeight(13.px)
            if (outline) {
                color(tag.outlineForegroundColor)
                backgroundColor(tag.outlineBackgroundColor)
                border {
                    width(1.px)
                    style(LineStyle.Solid)
                    color(tag.outlineBorderColor)
                }
            } else {
                color(tag.solidForegroundColor)
                backgroundColor(tag.solidBackgroundColor)
                border {
                    width(1.px)
                    style(LineStyle.Solid)
                    color(tag.solidBorderColor)
                }
            }
        }
    }) {
        Div({
            style {
                display(DisplayStyle.Flex)
                fontSize(12.px)
                fontWeight(700)
                textOverflow()
            }
        }) {
            Text(tag.name)
        }
    }
}


@Suppress("PublicApiImplicitType")
object ClickupStyleSheet : StyleSheet() {

}
