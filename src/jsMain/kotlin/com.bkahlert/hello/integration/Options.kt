package com.bkahlert.hello.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.Brand
import com.bkahlert.hello.Failure
import com.bkahlert.hello.ProfileState
import com.bkahlert.hello.ProfileState.Disconnected
import com.bkahlert.hello.ProfileState.Failed
import com.bkahlert.hello.ProfileState.Loading
import com.bkahlert.hello.ProfileState.Ready
import com.bkahlert.hello.Response
import com.bkahlert.hello.Session
import com.bkahlert.hello.Success
import com.bkahlert.hello.center
import com.bkahlert.hello.clickup.Tag
import com.bkahlert.hello.clickup.Team
import com.bkahlert.hello.clickup.TimeEntry
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.clickup.rest.AccessToken
import com.bkahlert.hello.visualize
import com.bkahlert.kommons.coerceAtMost
import com.bkahlert.kommons.fix.value
import com.bkahlert.kommons.time.toMoment
import kotlinx.browser.window
import kotlinx.coroutines.launch
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

@Composable
fun ErrorMessage(
    throwable: Throwable,
) {
    ErrorMessage {
        console.error(throwable)
        Span({
            title(throwable.stackTraceToString())
            style {
                cursor("help")
            }
        }) { Text(throwable.message ?: throwable.toString()) }
    }
}

@Composable
fun ErrorMessage(
    content: ContentBuilder<HTMLElement>? = null,
) {
    B({
        classes(OptionsStyleSheet.header)
        style {
            color(Brand.colors.red)
        }
    }, content)
}

@Composable
fun ClickUp(
    profileState: ProfileState,
    onConnect: (details: (defaultAccessToken: AccessToken?, callback: (AccessToken) -> Unit) -> Unit) -> Unit,
    onTeamSelect: (User, Team) -> Unit,
    session: Session?,
) {
    Style(OptionsStyleSheet)

    Div({
        style {
            center()
        }
    }) {
        when (profileState) {
            Disconnected -> B({
                classes(OptionsStyleSheet.header)
            }) {
                Connect(onConnect = onConnect)
            }

            Loading -> B({
                classes(OptionsStyleSheet.header)
            }) {
                Text("Loading")
            }

            is Ready -> {
                if (session != null) Session(session)
                else Profile(profileState, onTeamSelect)
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
fun Profile(
    profileState: Ready,
    onTeamSelect: (User, Team) -> Unit,
) {
    B { Text("Hi, ${profileState.user.username}") }
    val teams = profileState.teams
    when (teams.size) {
        1 -> {
            val team = teams.first()
            B { Text(team.name) }
            onTeamSelect(profileState.user, team)
        }
        else -> Select({
            onChange { event ->
                val teamId = Team.ID(checkNotNull(event.value) { "missing option value" })
                val team = profileState.teams.first { it.id == teamId }
                onTeamSelect(profileState.user, team)
            }
        }) {
            profileState.teams.forEach {
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
    session: Session,
) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            flexWrap(FlexWrap.Nowrap)
            justifyContent(JustifyContent.Center)
            alignContent(AlignContent.Start)
            alignItems(AlignItems.Start)
        }
    }) {
        Team(session.team)
        session.tasks.visualize { TaskCount(it.size) }
        ActiveTask(session.runningTimeEntry)
        session.spaces.visualize { spaces ->
            Ul {
                spaces.forEach { space ->
                    Li {
                        Text(space.name)
                        when (val lists = session.lists[space]) {
                            null -> {}
                            is Success -> {
                                lists.value.forEach {
                                    Text(" ")
                                    Span {
                                        Text(it.name)
                                        Text(" ")
                                        TaskCount(it.taskCount)
                                    }
                                }
                            }
                            is Failure -> ErrorMessage(lists.right)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveTask(runningTimeEntry: Response<TimeEntry?>) {
    runningTimeEntry.visualize(false) {
        B {
            Em { Small { Text("Active") } }
            Br()
            if (timeEntry != null) {
                Span({
                    style { color(Brand.colors.red) }
                }) {
                    timeEntry.taskUrl?.also { url ->
                        A(url.toString()) { Text(timeEntry.task.name) }
                    } ?: Text(timeEntry.task.name)
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
                                attr("xlink:href", "svg-sprite-cu2-tags")
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
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            flexWrap(FlexWrap.Nowrap)
            justifyContent(JustifyContent.Center)
            alignContent(AlignContent.Start)
            alignItems(AlignItems.Center)
        }
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

object OptionsStyleSheet : StyleSheet() {

    val header by style {
        textAlign("center")
        fontSize(1.em)
    }
}
