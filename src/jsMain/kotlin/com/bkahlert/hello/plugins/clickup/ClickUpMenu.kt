package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Failed
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelecting
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Disconnected
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioning
import com.bkahlert.hello.plugins.clickup.menu.Activity
import com.bkahlert.hello.plugins.clickup.menu.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.menu.ActivityDropdown
import com.bkahlert.hello.plugins.clickup.menu.ActivityGroup
import com.bkahlert.hello.plugins.clickup.menu.ConfigurationModal
import com.bkahlert.hello.plugins.clickup.menu.FailureModal
import com.bkahlert.hello.plugins.clickup.menu.MetaItems
import com.bkahlert.hello.plugins.clickup.menu.rememberActivityDropdownState
import com.bkahlert.hello.ui.AcousticFeedback
import com.bkahlert.hello.ui.DimmingLoader
import com.bkahlert.hello.ui.textOverflow
import com.bkahlert.kommons.dom.open
import com.bkahlert.kommons.time.Now
import com.clickup.api.Tag
import com.clickup.api.TaskID
import com.clickup.api.TaskListID
import com.clickup.api.Team
import com.clickup.api.TeamID
import com.clickup.api.TimeEntry
import com.clickup.api.User
import com.clickup.api.closed
import com.clickup.api.rest.AccessToken
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticElementScope
import com.semanticui.compose.collection.AnkerItem
import com.semanticui.compose.collection.DropdownItem
import com.semanticui.compose.collection.LinkItem
import com.semanticui.compose.collection.Menu
import com.semanticui.compose.collection.MenuElement
import com.semanticui.compose.collection.MenuItemElement
import com.semanticui.compose.element.Icon
import com.semanticui.compose.module.Dimmer
import com.semanticui.compose.module.Menu
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.ATarget.Blank
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
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
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.paddingRight
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
fun SemanticElementScope<MenuElement, *>.DisconnectedItems(
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

    LinkItem({
        onClick { configuring = true }
    }) {
        Img(src = "clickup-icon.svg", alt = "ClickUp") { classes("mini") }
        Text("Connect to ClickUp")
    }
}

@Composable
fun SemanticElementScope<MenuElement, *>.TeamSelectingItems(
    state: TeamSelecting,
    onActivate: (TeamID) -> Unit = {},
) {
    if (state.teams.isEmpty()) {
        LinkItem({
            +Borderless
            +Disabled
        }) {
            Span {
                Text("No teams found")
            }
        }
    } else {
        LinkItem({
            +Borderless
            +Disabled
        }) {
            Span {
                Text("Select team:")
            }
        }
        state.teams.forEach { (id, name, _, avatar, _) ->
            LinkItem({
                +Borderless
                onClick { onActivate(id) }
            }) {
                Img(src = avatar.toString(), alt = "Team $name") {
                    classes("avatar")
                }
                Span {
                    Text(name)
                }
            }
        }
    }
}

@Composable
fun SemanticElementScope<MenuElement, *>.MainItems(
    user: User,
    teams: List<Team>,
    selectedTeam: Team?,
    onTeamSelect: (TeamID) -> Unit = {},
    onRefresh: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {

    DropdownItem({ +Borderless }) {
        Img(src = user.profilePicture.toString(), alt = "User ${user.username}") {
            classes("rounded", "avatar")
        }
        Icon("dropdown")
        Menu {
            if (selectedTeam != null) TeamSelectionItems(teams, selectedTeam, onTeamSelect)
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
            LinkItem({ +Disabled }) {
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
        else -> DropdownItem {
            Icon("dropdown")
            Span({ classes("text") }) { Text("Switch Team") }
            Menu {
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
    attrs: SemanticAttrBuilder<MenuItemElement, HTMLDivElement>? = null,
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
            Img(src = team.avatar.toString(), alt = "Team ${team.name}") {
                style { borderRadius(0.em) }
                classes("ui", "avatar", "image")
            }
        }
        Span({ style { property("padding-right", "3em") } }) {
            Text(team.name)
        }
    }
}

@Composable
fun SemanticElementScope<MenuElement, *>.ActivityItems(
    activityGroups: List<ActivityGroup>,
    selectedActivity: Activity<*>?,
    onSelect: (Selection) -> Unit,
    onCreateTask: (TaskListID, String) -> Unit,
    onCloseTask: (TaskID) -> Unit,
    onTimeEntryStart: (TaskID?, List<Tag>, Boolean) -> Unit,
    onTimeEntryStop: (TimeEntry, List<Tag>) -> Unit,
) {
    var clicked by remember(selectedActivity) { mutableStateOf(false) }
    LinkItem({
        +Borderless
        if (selectedActivity == null) +Disabled
        onClick { clicked = it.target == it.currentTarget }
    }) {
        when (selectedActivity) {
            is RunningTaskActivity -> {
                PomodoroTimer(
                    rememberPomodoroTimerState(
                        timeEntry = selectedActivity.timeEntry,
                        progressIndicating = false,
                        acousticFeedback = AcousticFeedback.PomodoroFeedback,
                        onStop = onTimeEntryStop,
                    ),
                    stop = { clicked },
                )
            }
            else -> {
                PomodoroStarter(
                    rememberPomodoroStarterState(
                        taskID = selectedActivity?.task?.id,
                        acousticFeedback = AcousticFeedback.PomodoroFeedback,
                        onStart = onTimeEntryStart,
                        onCloseTask = selectedActivity?.task?.takeUnless { it.status.closed }?.let { { onCloseTask(it.id) } },
                    ),
                    start = { clicked },
                )
            }
        }
    }
    LinkItem({
        +Borderless
        if (selectedActivity == null) classes("disabled")
        style {
            flex(1, 1)
            minWidth("0") // https://css-tricks.com/flexbox-truncated-text/
        }
    }) {
        ActivityDropdown(
            rememberActivityDropdownState(
                groups = activityGroups,
                selection = selectedActivity,
                onSelect = { _, activity -> onSelect(listOfNotNull(activity?.id)) },
                onCreate = { taskListID, name -> onCreateTask(taskListID, name ?: "new task created $Now") },
            )
        )
    }

    Menu({ +Direction.Right }) {
        if (selectedActivity != null) {
            MetaItems(selectedActivity.meta.reversed())

            selectedActivity.url?.also { url ->
                AnkerItem(url.toString(), {
                    style { paddingRight(.6.em) }
                    onClick {
                        @Suppress("SpellCheckingInspection")
                        val features = "popup=1,innerWidth=900,innerHeight=1200,top=400"
                        window.open(url, "ClickUp-task", features)

                        // can't put this in an else case as it's not even safe to assume
                        // that one gets a window reference in case of success
                        it.preventDefault()
                        it.stopPropagation()
                    }
                    target(Blank)
                }) {
                    Icon("external", "alternate") {
                        title("Open on ClickUp")
                    }
                }
            }
        }
    }
}

@Composable
fun ClickUpMenu(
    viewModel: ClickUpMenuViewModel = rememberClickUpMenuViewModel(),
) {

    Div { // wrapper to make SemanticUI remove margins
        when (val state = viewModel.state.collectAsState(Disabled).value) {
            is Transitioning -> {
                console.info("ClickUp menu is ${state::class.simpleName}")
                ClickUpMenu(viewModel, state.previousState, loading = true)
            }
            is Failed -> {
                console.warn("ClickUp menu failed state ${state.previousState::class.simpleName}")
                FailureModal(
                    operation = state.operation,
                    cause = state.cause,
                    onRetry = state.retry,
                    onIgnore = state.ignore,
                    onSignOut = viewModel::disconnect
                )
                ClickUpMenu(viewModel, state.previousState)
            }
            is Succeeded -> {
                console.info("ClickUp menu in state ${state::class.simpleName}")
                ClickUpMenu(viewModel, state)
            }
        }
    }
}

@Composable
fun ClickUpMenu(
    viewModel: ClickUpMenuViewModel,
    state: Succeeded,
    loading: Boolean = false,
) {
    Menu({
        +Size.Mini + Dimmable
        if (state is Disabled || state is Disconnected) {
            +Fluid
            classes("one", "item")
        }
    }) {
        DimmingLoader(loading)
        when (state) {
            Disabled -> {
                Dimmer({
                    +Active
                    +Inverted
                })
                DisconnectedItems(
                    onConnect = {},
                )
            }
            Disconnected -> {
                DisconnectedItems(
                    onConnect = viewModel::connect,
                )
            }
            is TeamSelecting -> {
                MainItems(
                    user = state.user,
                    teams = state.teams,
                    selectedTeam = null,
                    onTeamSelect = viewModel::selectTeam,
                    onRefresh = viewModel::refresh,
                    onSignOut = viewModel::disconnect,
                )
                TeamSelectingItems(
                    state = state,
                    onActivate = viewModel::selectTeam,
                )
            }
            is TeamSelected -> {
                MainItems(
                    user = state.user,
                    teams = state.teams,
                    selectedTeam = state.selectedTeam,
                    onTeamSelect = viewModel::selectTeam,
                    onRefresh = viewModel::refresh,
                    onSignOut = viewModel::disconnect,
                )
                ActivityItems(
                    activityGroups = state.activityGroups,
                    selectedActivity = state.selectedActivity,
                    onSelect = viewModel::select,
                    onCreateTask = viewModel::createTask,
                    onCloseTask = viewModel::closeTask,
                    onTimeEntryStart = viewModel::startTimeEntry,
                    onTimeEntryStop = viewModel::stopTimeEntry,
                )
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
