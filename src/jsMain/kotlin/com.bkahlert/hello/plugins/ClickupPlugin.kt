package com.bkahlert.hello.plugins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bkahlert.Brand
import com.bkahlert.hello.ProfileState
import com.bkahlert.hello.ProfileState.Disconnected
import com.bkahlert.hello.ProfileState.Failed
import com.bkahlert.hello.ProfileState.Loaded.Activated
import com.bkahlert.hello.ProfileState.Loaded.Activating
import com.bkahlert.hello.ProfileState.Loaded.Searchable
import com.bkahlert.hello.ProfileState.Loading
import com.bkahlert.hello.Response
import com.bkahlert.hello.center
import com.bkahlert.hello.clickup.Tag
import com.bkahlert.hello.clickup.Task
import com.bkahlert.hello.clickup.Team
import com.bkahlert.hello.clickup.TimeEntry
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.clickup.rest.AccessToken
import com.bkahlert.hello.visualize
import com.bkahlert.kommons.Color.RGB
import com.bkahlert.kommons.coerceAtMost
import com.bkahlert.kommons.time.toMoment
import kotlinx.browser.window
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
import org.jetbrains.compose.web.css.cursor
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
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.style
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.SearchInput
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.HTMLElement
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGUseElement

@Composable
fun ErrorMessage(
    message: String,
) {
    ErrorMessage { Text(message) }
}

val Throwable.errorMessage: String get() = message ?: toString()

@Composable
fun ErrorMessage(
    throwable: Throwable,
) {
    ErrorMessage {
        console.error("an error occurred", throwable)
        Span({
            title(throwable.stackTraceToString())
            style {
                cursor("help")
            }
        }) { Text(throwable.errorMessage) }
    }
}

@Composable
fun ErrorMessage(
    content: ContentBuilder<HTMLElement>? = null,
) {
    B({
        classes(ClickupStyleSheet.header)
        style {
            color(Brand.colors.red)
        }
    }, content)
}

@Composable
fun ClickUp(
    profileState: ProfileState,
    onConnect: (details: (defaultAccessToken: AccessToken?, callback: (AccessToken) -> Unit) -> Unit) -> Unit,
) {
    Style(ClickupStyleSheet)

    Div({
        style {
            center()
        }
    }) {
        when (profileState) {
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
                TeamActivation(profileState.user, profileState.teams) { profileState.activate(it) }
            }
            is Activated -> {
                Session(profileState)
            }
            is Failed -> ErrorMessage {
                Text(profileState.message)
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

        if (session !is Searchable) session.prepare() else {
            AllTasks(session.tasks)
        }
    }
}

@Composable
fun AllTasks(
    tasksResponse: Response<List<Task>>,
) {
    val query = remember { mutableStateOf("") }
    tasksResponse.visualize(false) { tasks ->
        Div({
            classes(ClickupStyleSheet.sessionTasks)
        }) {
            SearchInput(query.value) {
                onInput { query.value = it.value }
            }
            TaskCount(tasks.size)
            Select({
                style {
                    maxWidth(400.px)
                }
            }) {
                tasks
                    .filter { it.name.contains(query.value, ignoreCase = true) }
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
                                    whiteSpace("nowrap")
                                    overflow("hidden")
                                    property("text-overflow", "ellipsis")
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
                color(tag.foregroundColor)
                backgroundColor(tag.foregroundColor.transparentize(.2))
                border {
                    width(1.px)
                    style(LineStyle.Solid)
                    color(tag.foregroundColor)
                }
            } else {
                color(Color.white)
                val backgroundColor = tag.backgroundColor.toHSL().coerceAtMost(lightness = 67.0)
                backgroundColor(backgroundColor)
                border {
                    width(1.px)
                    style(LineStyle.Solid)
                    color(backgroundColor)
                }
            }
        }
    }) {
        Div({
            style {
                display(DisplayStyle.Flex)
                fontSize(12.px)
                fontWeight(700)
                whiteSpace("nowrap")
                overflow("hidden")
                property("text-overflow", "ellipsis")
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

object ClickupStyleSheet : StyleSheet() {

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
