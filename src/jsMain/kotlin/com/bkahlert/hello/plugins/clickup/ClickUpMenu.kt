package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.plugins.clickup.ClickUpState.Connected.TeamSelected
import com.bkahlert.hello.plugins.clickup.ClickUpState.Connected.TeamSelecting
import com.bkahlert.hello.plugins.clickup.ClickUpState.Disconnected
import com.bkahlert.hello.plugins.clickup.ClickUpState.Failed
import com.bkahlert.hello.plugins.clickup.ClickUpState.Paused
import com.bkahlert.hello.plugins.clickup.menu.Activity
import com.bkahlert.hello.plugins.clickup.menu.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.menu.ActivityDropdown
import com.bkahlert.hello.plugins.clickup.menu.ActivityGroup
import com.bkahlert.hello.plugins.clickup.menu.ConfigurationModal
import com.bkahlert.hello.plugins.clickup.menu.ErrorItem
import com.bkahlert.hello.plugins.clickup.menu.MetaItems
import com.bkahlert.hello.plugins.clickup.menu.selected
import com.bkahlert.hello.ui.AcousticFeedback
import com.bkahlert.hello.ui.ErrorMessage
import com.bkahlert.hello.ui.textOverflow
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
import com.semanticui.compose.Variation.Size.Mini
import com.semanticui.compose.collection.DropdownItem
import com.semanticui.compose.collection.LinkItem
import com.semanticui.compose.collection.Menu
import com.semanticui.compose.collection.MenuElement
import com.semanticui.compose.collection.SubMenu
import com.semanticui.compose.element.Icon
import com.semanticui.compose.view.Item
import com.semanticui.compose.view.ItemElement
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
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
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.minWidth
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
        style {
            height(33.4.px)
            marginTop(1.cssRem)
        }
    }) {
        Div({
            classes("line")
            style { height(0.px) }
        })
    }
}

@Composable
fun DisconnectedClickupMenu(
    onConnect: (AccessToken) -> Unit = {},
) {
    var configuring by remember { mutableStateOf(false) }

    if (configuring) {
        ConfigurationModal(
            onConnect = {
                configuring = false
                onConnect(it)
            },
            onCancel = { configuring = false },
        )
    }

    Menu({
        +Size.Mini + Fluid
        if (configuring) +Disabled
        classes("one", "item")
    }) {
        LinkItem({
            onClick { configuring = true }
        }) {
            Img(src = "clickup-icon.svg", alt = "ClickUp") { classes("mini") }
            Text("Connect to ClickUp")
        }
    }
}


@Composable
fun ConnectingClickupMenu() {
    Menu({
        +Size.Mini + Fluid
        classes("one", "item")
    }) {
        LinkItem({
            +Disabled
            onClick {
                it.preventDefault()
            }
        }) {
            Icon("circle", "notch", { +Loading })
            Text("Connecting ...")
        }
    }
}

@Composable
fun FailedClickupMenu(
    state: Failed,
    onConnect: (AccessToken) -> Unit,
) {
    Menu({ +Size.Mini }) {

        var configuring by remember { mutableStateOf(false) }

        if (configuring) {
            ConfigurationModal(
                onConnect = {
                    configuring = false
                    onConnect(it)
                },
                onCancel = { configuring = false },
            )
        }

        val exception = state.exception
        ErrorItem(exception)
        SubMenu({ variation(Direction.Right) }) {
            LinkItem({
                onClick {
                    onConnect(state.accessToken)
                }
            }) {
                Text("Retry")
            }
            LinkItem({
                onClick {
                    configuring = true
                }
            }) {
                Text("Reconfigure")
            }
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
    onRefresh: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {

    DropdownItem(Unit, { _, _, _ -> }, { +Borderless }) {
        Img(src = user.profilePicture.toString(), alt = "User ${user.username}") {
            classes("avatar")
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
                onClick { onSignOut() }
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
fun SemanticElementScope<MenuElement, *>.ClickMenuLoadingActivityItems(
    activityGroups: Result<List<ActivityGroup>>?,
    onSelect: (Selection) -> Unit,
    onTimeEntryStart: (TaskID, List<Tag>, Boolean) -> Unit,
    onTimeEntryStop: (TimeEntry, List<Tag>) -> Unit,
) {
    if (activityGroups != null) activityGroups.fold(
        {
            ClickupMenuActivityItems(
                activityGroups = it,
                onSelect = onSelect,
                onTimeEntryStart = onTimeEntryStart,
                onTimeEntryStop = onTimeEntryStop,
            )
        },
        {
            ErrorMessage(it)
        },
    ) else {
        Text("Loading...")
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
        +Borderless
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
fun ClickUpMenu(
    model: ClickUpModel = remember { ClickUpModel() },
) {
    Style(ClickupStyleSheet)

    val _state by model.menuState.collectAsState(Paused)
    console.info("ClickUp menu in state ${_state::class.simpleName}")

    var loading by remember(_state) { mutableStateOf(false) }
    if (loading) {
        ConnectingClickupMenu()
    } else {
        when (val state = _state) {
            Paused -> InitializingClickupMenu()
            Disconnected -> DisconnectedClickupMenu {
                loading = true
                model.connect(it)
            }
            is Failed -> {
                FailedClickupMenu(
                    state = state,
                ) {
                    loading = true
                    model.connect(it)
                }
            }
            is TeamSelecting -> {
                Menu({ +Mini }) {
                    ClickupMenuTeamSelectingItems(
                        state = state,
                        onActivate = model::selectTeam,
                    )
                }
            }
            is TeamSelected -> {
                Menu({ +Mini }) {
                    ClickupMenuMainItems(
                        user = state.user,
                        teams = state.teams,
                        selectedTeam = state.selectedTeam,
                        onTeamSelect = model::selectTeam,
                        onRefresh = { model.refresh(force = true) },
                        onSignOut = { model.signOut() },
                    )
                    ClickMenuLoadingActivityItems(
                        activityGroups = state.activityGroups,
                        onSelect = model::select,
                        onTimeEntryStart = model::startTimeEntry,
                        onTimeEntryStop = { _, tags -> model.stopTimeEntry(tags) },
                    )
                }

                DisposableEffect(state.selectedTeam) {
                    model.refresh()
                    onDispose { }
                }
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
