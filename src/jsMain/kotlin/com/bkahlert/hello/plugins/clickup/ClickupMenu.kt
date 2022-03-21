package com.bkahlert.hello.plugins.clickup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.Failure
import com.bkahlert.hello.Success
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Disconnected
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Failed
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Initializing
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activated
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activated.Activity
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activated.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loaded.Activating
import com.bkahlert.hello.plugins.clickup.ClickupMenuState.Loading
import com.bkahlert.hello.ui.ErrorMessage
import com.bkahlert.hello.ui.textOverflow
import com.bkahlert.kommons.coroutines.flow.toStringAndHash
import com.bkahlert.kommons.fix.value
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.rest.AccessToken
import com.semanticui.compose.State
import com.semanticui.compose.Variation
import com.semanticui.compose.Variation.Colored.Blue
import com.semanticui.compose.Variation.Colored.Red
import com.semanticui.compose.Variation.Columns.One
import com.semanticui.compose.Variation.Position.Bottom
import com.semanticui.compose.Variation.Position.Right
import com.semanticui.compose.Variation.Size.Mini
import com.semanticui.compose.collection.DropdownItem
import com.semanticui.compose.collection.LinkItem
import com.semanticui.compose.collection.Menu
import com.semanticui.compose.collection.SubMenu
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconGroup
import com.semanticui.compose.element.Input
import com.semanticui.compose.module.Divider
import com.semanticui.compose.module.DropdownText
import com.semanticui.compose.module.Header
import com.semanticui.compose.module.InlineDropdown
import com.semanticui.compose.view.Item
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
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
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.style
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

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
    Menu({ variation(Mini) }) {
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
fun ActivatingClickupMenu(
    state: Activating,
    onActivate: (Team.ID) -> Unit = {},
) {
    Menu({ variation(Mini) }) {
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

    if (state.teams.size == 1) {
        onActivate(state.teams.first().id)
    }
}

@Composable
fun ActivatedClickupMenu(
    state: Activated,
    onRefresh: () -> Unit = {},
    onTimeEntryStart: (Task.ID, List<Tag>, billable: Boolean) -> Unit = { _, _, _ -> },
    onTimeEntryAbort: (TimeEntry, List<Tag>) -> Unit = { _, _ -> },
    onTimeEntryComplete: (TimeEntry, List<Tag>) -> Unit = { _, _ -> },
) {
    var selectedActivity by remember { mutableStateOf<Activity<*>?>(null) }

    // pre-select always with running activity if any
    if (selectedActivity?.takeUnless { it is RunningTaskActivity } == null) {
        selectedActivity = state.runningActivity
    }

    console.warn("currently running ${state.runningActivity}")

    Menu({ variation(Mini) }) {
        DropdownItem(state, { _, _, _ -> }, {
            variation(Borderless, Variation.Icon)
        }) {
            Icon("youtube")
            SubMenu {
                Item {
                    Icon("dropdown")
                    Span({ classes("text") }) { Text("Switch Team") }
                    SubMenu {
                        Item {
                            Icon("grid", "layout")
                            Text("Team A")
                        }
                        Item {
                            Icon("grid", "layout")
                            Text("Team B")
                        }
                    }
                }
                Item {
                    Icon("sign-out")
                    Text("Sign-out")
                }
            }
        }
        when (val tasks = state.tasks) {
            null -> {
                DropdownItem(state, { _, _, _ -> }, {
                    variation(Borderless, Variation.Icon)
                    state(State.Loading, State.Disabled)
                }) {
                    Icon("stopwatch")
                }

                // TODO merge with success case
                LinkItem({
                    variation(Borderless)
                    if (selectedActivity == null) state(State.Disabled)
                }) {
                    // TODO use visualize to simplify error handling
                    when (state.runningTimeEntry) {
                        null -> Icon("green", "play", "disabled")
                        is Failure -> IconGroup {
                            Icon("green", "play")
                            Icon("exclamation", "circle", { variation(Red, Bottom, Right, Corner) })
                        }
                        is Success -> {
                            when (val runningTimeEntry = state.runningTimeEntry.value) {
                                null -> {
                                    PomodoroStarter(selectedActivity?.taskID, onStart = onTimeEntryStart)
                                }
                                else -> PomodoroTimer(
                                    timeEntry = runningTimeEntry,
                                    onAbort = onTimeEntryAbort,
                                    onComplete = onTimeEntryComplete,
                                )
                            }
                        }
                    }
                }
                onRefresh()
            }
            is Success -> {
                DropdownItem(state, { value, _, _ ->
                    selectedActivity = state.activity(value)
                }, {
                    variation(Borderless, Variation.Icon)
                }) {
                    Icon("stopwatch")
                    SubMenu {

                        Header { Text("Start new pomodoro") }
                        Input({ variation(Variation.Icon("search")) }) {
                            Input(InputType.Text) { placeholder("Search tasks...") }
                            Icon("search")
                        }
                        Divider()

                        tasks.value.groupBy { it.list }.forEach { (list, listTasks) ->
                            Header({ variation(Blue) }) {
                                Icon("list")
                                Text(list?.name ?: "?")
                            }  // TODO title or tooltip
                            listTasks.forEach { task ->
                                Item({
                                    attr("data-value", task.id.stringValue)
                                    style {
                                        maxWidth(200.px)
                                        textOverflow()
                                    }
                                }) {
                                    TaskIcon(task)
                                    Text(task.name)
                                }
                            }
                        }
                    }
                }
                LinkItem({
                    variation(Borderless)
                    if (selectedActivity == null) state(State.Disabled)
                }) {
                    // TODO use visualize to simplify error handling
                    when (state.runningTimeEntry) {
                        null -> Icon("green", "play", "disabled")
                        is Failure -> IconGroup {
                            Icon("green", "play")
                            Icon("exclamation", "circle", { variation(Red, Bottom, Right, Corner) })
                        }
                        is Success -> {
                            when (val runningTimeEntry = state.runningTimeEntry.value) {
                                null -> {
                                    PomodoroStarter(selectedActivity?.taskID, onStart = onTimeEntryStart)
                                }
                                else -> PomodoroTimer(
                                    runningTimeEntry,
                                    onAbort = onTimeEntryAbort,
                                    onComplete = onTimeEntryComplete,
                                )
                            }
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
                    selectedActivity?.also { TaskIcon(it) } ?: Icon({ variation(Inverted) })
                    InlineDropdown(state, { value, _, _ ->
                        selectedActivity = state.activity(value)
                    }, {
                        variation(Variation.Scrolling)
                        style {
                            flex(1, 1)
                            minWidth("0") // https://css-tricks.com/flexbox-truncated-text/
                        }
                    }) {
                        DropdownText({
                            style {
                                maxWidth(100.percent)
                                textOverflow()
                            }
                        }) {
                            when (val task = selectedActivity) {
                                null -> Text("Select task...")
                                else -> Text(task.name)
                            }
                        }
                        SubMenu({
                            style { maxWidth(200.percent) }
                        }) {
                            Input({ variation(Variation.Icon("search")) }) {
                                Input(InputType.Text) { placeholder("Search tasks...") }
                                Icon("search")
                            }

                            state.activities.onEach { (meta, color, activities) ->
                                Divider()
                                Header({ style { color?.also { color(it) } } }) {
                                    meta.forEachIndexed { index, meta ->
                                        if (index > 0) Icon("inverted")
                                        Icon({
                                            title(meta.title)
                                            style { classes(*meta.iconVariations.toTypedArray()) }
                                        })
                                        meta.text?.also { Text(it) }
                                    }
                                }
                                activities.forEach { activity ->
                                    ActivityItem(activity)
                                }
                            }
                        }
                    }
                }

                SubMenu({ variation(Direction.Right) }) {
                    selectedActivity?.meta?.forEach {
                        Item({ variation(Borderless) }) {
                            MetaIcon(it)
                        }
                    }
                }
            }
            is Failure -> DropdownItem(state, { _, _, _ -> }, {
                variation(Borderless, Variation.Icon)
            }) {
                IconGroup {
                    Icon("youtube")
                    Icon("exclamation", "circle", { variation(Red, Bottom, Right, Corner) })
                }
                SubMenu {
                    ErrorMessage(tasks.value)
                }
            }
        }
    }
}

@Composable
fun FailedClickupMenu(
    state: Failed,
) {
    Menu({ variation(Mini) }) {
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
}

@Composable
fun ClickupMenu(
    state: ClickupMenuState,
    onConnect: (details: (defaultAccessToken: AccessToken?, callback: (AccessToken) -> Unit) -> Unit) -> Unit = {},
    onActivate: (Team.ID) -> Unit = {},
    onRefresh: () -> Unit = {},
    onTimeEntryStart: (Task.ID, List<Tag>, billable: Boolean) -> Unit = { _, _, _ -> },
    onTimeEntryAbort: (TimeEntry, List<Tag>) -> Unit = { _, _ -> },
    onTimeEntryComplete: (TimeEntry, List<Tag>) -> Unit = { _, _ -> },
) {
    console.warn("STATE ${state.toStringAndHash()}")
    when (state) {
        Initializing -> InitializingClickupMenu()
        Disconnected -> DisconnectedClickupMenu(onConnect)
        Loading -> LoadingClickupMenu()
        is Activating -> ActivatingClickupMenu(
            state = state,
            onActivate = onActivate,
        )
        is Activated -> ActivatedClickupMenu(
            state = state,
            onRefresh = onRefresh,
            onTimeEntryStart = onTimeEntryStart,
            onTimeEntryAbort = onTimeEntryAbort,
            onTimeEntryComplete = onTimeEntryComplete,
        )
        is Failed -> FailedClickupMenu(state)
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
