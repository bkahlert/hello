package com.bkahlert.hello.clickup.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.clickup.model.ClickUpClient
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.TaskID
import com.bkahlert.hello.clickup.model.TaskListID
import com.bkahlert.hello.clickup.model.Team
import com.bkahlert.hello.clickup.model.TeamID
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.User
import com.bkahlert.hello.clickup.model.closed
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.hello.clickup.view.Activity
import com.bkahlert.hello.clickup.view.Activity.RunningTaskActivity
import com.bkahlert.hello.clickup.view.ActivityDropdown
import com.bkahlert.hello.clickup.view.ActivityGroup
import com.bkahlert.hello.clickup.view.ConfigurationModal
import com.bkahlert.hello.clickup.view.Configurer
import com.bkahlert.hello.clickup.view.FailureModal
import com.bkahlert.hello.clickup.view.MetaItems
import com.bkahlert.hello.clickup.view.rememberActivityDropdownState
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Failed
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelected
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Connected.TeamSelecting
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioned.Succeeded.Disconnected
import com.bkahlert.hello.clickup.viewmodel.ClickUpMenuState.Transitioning
import com.bkahlert.kommons.Now
import com.bkahlert.kommons.dom.open
import com.bkahlert.semanticui.collection.AnchorItem
import com.bkahlert.semanticui.collection.LinkItem
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.MenuElement
import com.bkahlert.semanticui.collection.MenuItemDivElement
import com.bkahlert.semanticui.collection.borderless
import com.bkahlert.semanticui.collection.disabled
import com.bkahlert.semanticui.collection.target
import com.bkahlert.semanticui.core.attributes.Modifier
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.core.attributes.raw
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.custom.MixBlendMode.Companion.Luminosity
import com.bkahlert.semanticui.custom.apply
import com.bkahlert.semanticui.custom.backgroundColor
import com.bkahlert.semanticui.custom.color
import com.bkahlert.semanticui.custom.mixBlendMode
import com.bkahlert.semanticui.custom.textOverflow
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.size
import com.bkahlert.semanticui.module.DropdownItem
import com.bkahlert.semanticui.module.inverted
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.ATarget.Blank
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
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

@Composable
public fun SemanticElementScope<MenuElement>.DisconnectedItems(
    onConnect: (ClickUpClient) -> Unit = {},
    vararg configurers: Configurer<ClickUpClient>,
) {
    var configuring by remember { mutableStateOf(false) }

    if (configuring) {
        ConfigurationModal(
            onConnect = {
                configuring = false
                onConnect(it)
            },
            onCancel = {
                configuring = false
            },
            configurers = configurers,
        )
    }

    LinkItem({
        if (configurers.isEmpty()) s.disabled()
        else onClick { configuring = true }
    }) {
        Img(src = ImageFixtures.ClickUpMark.toString(), alt = "ClickUp") {
            classes("mini")
            style {
                mixBlendMode(Luminosity)
            }
        }
        Text("Connect to ClickUp")
    }
}

@Composable
public fun SemanticElementScope<MenuElement>.TeamSelectingItems(
    state: TeamSelecting,
    onActivate: (TeamID) -> Unit = {},
) {
    if (state.teams.isEmpty()) {
        LinkItem({
            s.disabled()
            v.borderless()
        }) {
            Span {
                Text("No teams found")
            }
        }
    } else {
        LinkItem({
            s.disabled()
            v.borderless()
        }) {
            Span {
                Text("Select team:")
            }
        }
        state.teams.forEach { (id, name, _, avatar, _) ->
            LinkItem({
                v.borderless()
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
public fun SemanticElementScope<MenuElement>.MainItems(
    user: User,
    teams: List<Team>,
    selectedTeam: Team?,
    onTeamSelect: (TeamID) -> Unit = {},
    onRefresh: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {

    DropdownItem({ raw(Modifier.Variation.Borderless) }) {
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
public fun SemanticElementScope<MenuElement>.TeamSelectionItems(
    teams: List<Team>,
    selectedTeam: Team?,
    onTeamSelect: (TeamID) -> Unit,
) {
    when (teams.size) {
        0 -> {
            LinkItem({ raw(Modifier.State.Disabled) }) {
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
                if (team.id == selectedTeam?.id) {
                    s.disabled()
                    raw(Modifier.State.Active)
                }
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
                        if (team.id == selectedTeam?.id) {
                            s.disabled()
                            raw(Modifier.State.Active)
                        }
                    }
                }
            }
        }
    }
}

@Composable
public fun SemanticElementScope<MenuElement>.TeamItem(
    team: Team,
    onClick: () -> Unit,
    attrs: SemanticAttrBuilderContext<MenuItemDivElement>? = null,
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
public fun SemanticElementScope<MenuElement>.ActivityItems(
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
        v.borderless()
        if (selectedActivity == null) raw(Modifier.State.Disabled)
        onClick { clicked = it.target == it.currentTarget }
    }) {
        when (selectedActivity) {
            is RunningTaskActivity -> {
                PomodoroTimer(
                    rememberPomodoroTimerState(
                        timeEntry = selectedActivity.timeEntry,
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
        v.borderless()
        if (selectedActivity == null) raw(Modifier.State.Disabled)
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

    Menu({ classes("right") }) {
        if (selectedActivity != null) {
            MetaItems(selectedActivity.meta.reversed())

            selectedActivity.url?.also { url ->
                AnchorItem(url.toString(), {
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
public fun ClickUpMenu(
    viewModel: ClickUpMenuViewModel = rememberClickUpMenuViewModel(),
) {

    Div({ classes("clickup-toolbar") }) {
        when (val state = viewModel.state.collectAsState(Disabled).value) {
            is Transitioning -> {
                console.info("ClickUp menu is ${state::class.simpleName}")
                ClickUpMenu(viewModel, state.previousState, loadingState = LoadingState.On)
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
public fun ClickUpMenu(
    viewModel: ClickUpMenuViewModel,
    state: Succeeded,
    loadingState: LoadingState = LoadingState.Off,
) {
    Menu({
        apply(loadingState)
        raw(Mini)
        if (state is Disabled || state is Disconnected) {
            raw(Modifier.Variation.Fluid)
            classes("one", "item")
        }
    }) {
        apply(loadingState, dimmerAttrs = { v.inverted() }, loaderAttrs = { v.size(Mini) })
        when (state) {
            Disabled -> DisconnectedItems()

            Disconnected -> {
                DisconnectedItems(
                    onConnect = viewModel::connect,
                    configurers = viewModel.configurers,
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
public fun Tag(
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
