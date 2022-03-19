package com.bkahlert.hello.plugins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.Brand
import com.bkahlert.hello.Failure
import com.bkahlert.hello.Response
import com.bkahlert.hello.Success
import com.bkahlert.hello.plugins.clickup.ActivityItem
import com.bkahlert.hello.plugins.clickup.ClickupState
import com.bkahlert.hello.plugins.clickup.ClickupState.Disconnected
import com.bkahlert.hello.plugins.clickup.ClickupState.Failed
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activated
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activated.Activity
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activated.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activating
import com.bkahlert.hello.plugins.clickup.ClickupState.Loading
import com.bkahlert.hello.plugins.clickup.MetaIcon
import com.bkahlert.hello.plugins.clickup.PomodoroTimer
import com.bkahlert.hello.plugins.clickup.TaskIcon
import com.bkahlert.hello.ui.ErrorMessage
import com.bkahlert.hello.ui.textOverflow
import com.bkahlert.hello.ui.visualize
import com.bkahlert.kommons.Color.RGB
import com.bkahlert.kommons.fix.orNull
import com.bkahlert.kommons.fix.value
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.toMoment
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.User
import com.clickup.api.rest.AccessToken
import com.semanticui.compose.State
import com.semanticui.compose.Variation
import com.semanticui.compose.Variation.Borderless
import com.semanticui.compose.Variation.Colored.Blue
import com.semanticui.compose.Variation.Colored.Green
import com.semanticui.compose.Variation.Colored.Red
import com.semanticui.compose.Variation.Columns.One
import com.semanticui.compose.Variation.Corner
import com.semanticui.compose.Variation.Direction
import com.semanticui.compose.Variation.Floating
import com.semanticui.compose.Variation.Fluid
import com.semanticui.compose.Variation.Position.Bottom
import com.semanticui.compose.Variation.Position.Right
import com.semanticui.compose.Variation.Size.Mini
import com.semanticui.compose.Variation.Size.Small
import com.semanticui.compose.collection.DropdownItem
import com.semanticui.compose.collection.LinkItem
import com.semanticui.compose.collection.Menu
import com.semanticui.compose.collection.SubMenu
import com.semanticui.compose.element.Icon
import com.semanticui.compose.element.IconGroup
import com.semanticui.compose.element.Input
import com.semanticui.compose.module.Divider
import com.semanticui.compose.module.Dropdown
import com.semanticui.compose.module.Dropdown.Type.SearchSelection
import com.semanticui.compose.module.DropdownText
import com.semanticui.compose.module.Header
import com.semanticui.compose.module.InlineDropdown
import com.semanticui.compose.view.Item
import io.ktor.client.fetch.get
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.style
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.SearchInput
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGUseElement

@Composable
fun ClickUp(
    clickupState: ClickupState,
    onConnect: (details: (defaultAccessToken: AccessToken?, callback: (AccessToken) -> Unit) -> Unit) -> Unit,
) {

    var selectedPomodoroType by remember { mutableStateOf(Pomodoro.Type.values().first()) }

    when (clickupState) {
        Disconnected -> {

            Menu({ variation(Mini) }) {
                DropdownItem(clickupState, { _, _, _ -> }, { variation(Borderless, Variation.Icon) }) {
                    Icon("youtube")
                    SubMenu {
                        Item {
                            Icon("sign-in")
                            Text("Sign-in")
                        }
                    }
                }
                DropdownItem(clickupState, { _, _, _ -> }, {
                    variation(Borderless, Variation.Icon)
                    state(State.Disabled)
                }) {
                    Icon("stopwatch")
                }
            }
        }

        Loading -> {
            Menu({
                variation(Mini, Fluid, One)
                classes("item")
            }) {
                DropdownItem(clickupState, { _, _, _ -> }, {
                    variation(Borderless, Variation.Icon)
                    state(State.Loading, State.Disabled)
                }) {
                    Icon("youtube")
                }
            }
        }

        is Activating -> {
            Menu({ variation(Mini) }) {
                DropdownItem(clickupState, { _, _, _ -> }, {
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
                LinkItem({ variation(Borderless) }) {
                    Icon("grid", "layout")
                    Text("Team A")
                }
                LinkItem({ variation(Borderless) }) {
                    Icon("grid", "layout")
                    Text("Team B")
                }
            }

            if (clickupState.teams.size == 1) {
                clickupState.activate(clickupState.teams.first())
            }
        }

        is Activated -> {
            val selectedActivity = remember { mutableStateOf<Activity<*>?>(null) }

            clickupState.runningTimeEntry?.also { it.orNull()?.let { PomodoroTimer(it) } }

            // pre-select always with running activity if any
            if (selectedActivity.value?.takeUnless { it is RunningTaskActivity } == null) {
                selectedActivity.value = clickupState.runningActivity
            }

            Menu({ variation(Mini) }) {
                DropdownItem(clickupState, { _, _, _ -> }, {
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
                when (val tasks = clickupState.tasks) {
                    null -> {
                        DropdownItem(clickupState, { _, _, _ -> }, {
                            variation(Borderless, Variation.Icon)
                            state(State.Loading, State.Disabled)
                        }) {
                            Icon("stopwatch")
                        }
                        clickupState.prepare()
                    }
                    is Success -> {
                        DropdownItem(clickupState, { value, _, _ ->
                            selectedActivity.value = clickupState.activity(value)
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
                            if (selectedActivity.value == null) state(State.Disabled)
                        }) {
                            when (clickupState.runningTimeEntry) {
                                null -> Icon("green", "play", "disabled")
                                is Failure -> IconGroup {
                                    Icon("green", "play")
                                    Icon("exclamation", "circle", { variation(Red, Bottom, Right, Corner) })
                                }
                                is Success -> {
                                    when (val running = clickupState.runningTimeEntry.value) {
                                        null -> if (selectedActivity.value != null) {
                                            Icon("green", "play")
                                        } else {
                                            Icon("green", "play", "disabled")
                                        }
                                        else -> {
                                            Icon("red", "stop")
                                            Text(Now.toTimeString())
//                                            onTick()
                                        }
                                    }
                                }
                            }
                            InlineDropdown(clickupState.runningTimeEntry,
                                { value, _, _ -> selectedPomodoroType = enumValueOf(value) }) {
                                DropdownText { Text(selectedPomodoroType.duration.toMoment(false)) }
                                Icon("dropdown")
                                SubMenu {
                                    Pomodoro.Type.values().forEach {
                                        Item({ attr("data-value", it.name) }) { Text(it.duration.toMoment(false)) }
                                    }
                                }
                            }
                        }
                        Item({
                            variation(Borderless)
                            if (selectedActivity.value == null) state(State.Disabled)
                            style {
                                flex(1, 1)
                                minWidth("0") // https://css-tricks.com/flexbox-truncated-text/
                            }
                        }) {
                            selectedActivity.value?.also { TaskIcon(it) } ?: Icon({ variation(Inverted) })
                            InlineDropdown(clickupState, { value, _, _ ->
                                selectedActivity.value = clickupState.activity(value)
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
                                    when (val task = selectedActivity.value) {
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

                                    clickupState.activities.onEach { (meta, color, activities) ->
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
                            selectedActivity.value?.meta?.forEach {
                                Item({ variation(Borderless) }) {
                                    MetaIcon(it)
                                }
                            }
                        }
                    }
                    is Failure -> DropdownItem(clickupState, { _, _, _ -> }, {
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

        is Failed -> {
            Menu({ variation(Mini) }) {
                DropdownItem(clickupState, { _, _, _ -> }, {
                    variation(Borderless, Floating, Variation.Icon)
                }) {
                    IconGroup {
                        Icon("youtube")
                        Icon("exclamation", "circle", { variation(Red, Bottom, Right, Corner) })
                    }
                    SubMenu {
                        ErrorMessage(clickupState.exceptions.first())
                        Item {
                            Icon("sign-out")
                            Text("Sign-out")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClickUpDeprecated(
    clickupState: ClickupState,
    onConnect: (details: (defaultAccessToken: AccessToken?, callback: (AccessToken) -> Unit) -> Unit) -> Unit,
) {
    Style(ClickupStyleSheet)

    Div {
        when (clickupState) {
            Disconnected -> B({
                classes(ClickupStyleSheet.header)
            }) {
                Connect(onConnect = onConnect)
            }

            Loading -> B({
                classes(ClickupStyleSheet.header)
            }) {
                Text("Loading")
            }

            is Activating -> {
                TeamActivation(clickupState.user, clickupState.teams) { clickupState.activate(it) }
            }
            is Activated -> {
                Session(clickupState)
            }
            is Failed -> ErrorMessage {
                Text(clickupState.message)
                Br()
                Small { Connect("Try again...", onConnect = onConnect) }
            }

        }
    }
}

@Composable
fun Connect(
    text: String = "Connect...",
    onConnect: ((defaultAccessToken: AccessToken?, callback: (AccessToken) -> Unit) -> Unit) -> Unit,
) {
    // #svg-sprite-cu2-logo-icon
    A("#", {
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
        Text(text)
    }
}

@Composable
fun TeamActivation(
    user: User,
    teams: List<Team>,
    onTeamSelect: (Team) -> Unit,
) {
    B { Text("Hi, ${user.username}") }
    when (teams.size) {
        1 -> {
            val team = teams.first()
            B { Text(team.name) }
            onTeamSelect(team)
        }
        else -> Select({
            onChange { event ->
                val teamId = Team.ID(checkNotNull(event.value) { "missing option value" })
                val team = teams.first { it.id == teamId }
                onTeamSelect(team)
            }
        }) {
            // TODO show loading
            // TODO show error with https://semantic-ui.com/modules/dropdown.html#message / https://semantic-ui.com/modules/dropdown.html#error
            // TODO segment by space (using divider)
            // TODO filter by status
            // TODO show status using colored icon
            // TODO show time spent in description
            // TODO most recent at top
            teams.forEach {
                Option(it.id.id, {
                    style { backgroundColor(it.color) }
                }) {
                    Text(it.name)
                }
            }
        }
    }
}

@Composable
fun Session(
    session: Activated,
) {
    Div({
        classes(ClickupStyleSheet.session)
    }) {
        Team(session.activeTeam)

        CurrentTask(session.runningTimeEntry,
            onStart = { session.startTimeEntry() },
            onStop = { session.stopTimeEntry() })

        if (session.tasks == null) session.prepare() else {
            AllTasks(session.tasks)
        }
    }
}

@Composable
fun AllTasks(
    tasksResponse: Response<List<Task>>,
) {
    var query by remember { mutableStateOf("") }
    tasksResponse.visualize(false) { tasks ->
        Div({
            classes(ClickupStyleSheet.sessionTasks)
        }) {
            SearchInput(query) {
                onInput { query = it.value }
            }
            TaskCount(tasks.size)

            IconGroup {
                Icon("tag")
            }
            IconGroup {
                Icon("tag")
                Icon("plus", { variation(Green, Small, Bottom, Right, Corner) })
            }

            Dropdown(
                tasks, "Select Task",
                SearchSelection, Fluid,
                onChange = { value, text, ele ->
                    console.log("SELECTED", value, text, ele.length, ele.get(0))
                },
            ) { task ->
                // TODO task status
                Div({
                    classes("item")
                    attr("data-value", task.id.stringValue)
                }) {
                    TaskIcon(task)
                    Text(task.name)
                }
            }



            Select({
                style {
                    maxWidth(400.px)
                }
            }) {
                tasks
                    .filter { it.name.contains(query, ignoreCase = true) }
                    .forEach {
                        // TODO task status
                        Option(it.id.stringValue) {
                            Text(it.name)
                            Text(" (")
                            Text(it.status.status)
                            Text(")")
                        }
                    }
            }
        }
    }
}

@Composable
fun CurrentTask(
    runningTimeEntry: Response<TimeEntry?>,
    onStart: (TimeEntry) -> Unit = {}, // TODO refactor
    onStop: (TimeEntry) -> Unit = {},
) {
    runningTimeEntry.visualize(false) { timeEntry ->
        B({
            classes(ClickupStyleSheet.sessionCurrentTask)
        }) {
            Em { Small { Text("Active") } }
            Br()
            if (timeEntry != null) {
                Span({
                    style { color(Brand.colors.red) }
                }) {
                    val taskName = timeEntry.task?.name
                    timeEntry.taskUrl?.also { url ->
                        A(url.toString()) {
                            if (taskName != null) Text(taskName)
                            else Span({
                                style {
                                    fontSize(12.px)
                                    fontWeight(400)
                                    lineHeight(1.em)
                                    textOverflow()
                                    color(RGB(0x292d34))
                                }
                            }) { Text("(no task)") }
                        }
                    } ?: Text(taskName ?: "TODO")
                    Text(" ⏱ ")
                    Text(timeEntry.duration.absoluteValue.toMoment())
                    Text(" ")
                    Button({
                        onClick { onStop(timeEntry) }
                    }) {
                        Text("Stop")
                    }
                    Button({ // TODO refactor
                        onClick { onStart(timeEntry) }
                    }) {
                        Text("Start")
                    }
                }
                Ul {
                    Li {
                        Text("created")
                        Text(" ")
                        Text(timeEntry.at.toMoment())
                    }
                    Li {
                        Text("time estimated")
                        Text(" ")
                        Text("5h")
                    }
                    Li {
                        Text("time spent")
                        Text(" ")
                        Text("3h 20m")
                    }
                    Li {
                        TagElement<SVGElement>("svg", {}) {
                            TagElement<SVGUseElement>("use", {
                                attr("href", "clickup-symbols.svg#svg-sprite-cu2-tag-o")
                            }) {

                            }
                        }
                        TagElement<SVGElement>("svg", {}) {
                            TagElement<SVGUseElement>("use", {
                                attr("xlink:href", "clickup-symbols.svg#svg-sprite-cu2-tags")
                            }) {

                            }
                        }
                        Div({
                            style {
                                display(DisplayStyle.Flex)
                                flexWrap(FlexWrap.Wrap)
                                alignItems(AlignItems.Center)
                                marginLeft(10.px)
                            }
                        }) {
                            timeEntry.tags.forEach { tag ->
                                Tag(tag)
                            }
                        }
                    }
                }
            } else {
                Span({
                    style { color(Brand.colors.black.transparentize(.5)) }
                }) {
                    Em { Text("No active task") }
                }
            }
        }
    }
}

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

@Composable
fun Team(
    team: Team,
) {
    Div({
        classes(ClickupStyleSheet.sessionTeam)
    }) {
        Img(src = team.avatar.toString()) {
            style {
                width(1.cssRem)
                height(1.cssRem)
            }
        }
        B({
            style {
                flex(1, 0)
            }
        }) { Text(team.name) }
    }
}

@Composable
fun TaskCount(
    count: Int?,
) {
    Span({
        style {
            color(Brand.colors.blue)
        }
    }) {
        Text(count?.toString() ?: "unknown")
    }
}

@Suppress("PublicApiImplicitType")
object ClickupStyleSheet : StyleSheet() {

    @Deprecated("use semantic header")
    val header by style {
        textAlign("center")
        fontSize(1.em)
    }

    val session by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        flexWrap(FlexWrap.Nowrap)
        justifyContent(JustifyContent.Center)
        alignContent(AlignContent.Start)
        alignItems(AlignItems.Start)
        border {
            width(1.px)
            style(LineStyle.Solid)
            color(Color.red)
        }
    }

    val sessionTeam by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Row)
        flexWrap(FlexWrap.Nowrap)
        justifyContent(JustifyContent.Center)
        alignContent(AlignContent.Start)
        alignItems(AlignItems.Center)
        border {
            width(1.px)
            style(LineStyle.Solid)
            color(Color.magenta)
        }

    }

    val sessionTasks by style {
        border {
            width(1.px)
            style(LineStyle.Solid)
            color(Color.green)
        }

    }

    val sessionCurrentTask by style {
        border {
            width(1.px)
            style(LineStyle.Solid)
            color(Color.blue)
        }

    }
}
