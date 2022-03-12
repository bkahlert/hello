package com.bkahlert.hello.integration

import androidx.compose.runtime.Composable
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
import com.bkahlert.hello.clickup.AccessToken
import com.bkahlert.hello.clickup.Team
import com.bkahlert.hello.clickup.TimeEntry
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.visualize
import com.bkahlert.kommons.fix.value
import kotlinx.browser.window
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.HTMLElement

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
    ErrorMessage { Text(throwable.message ?: throwable.toString()) }
    console.error(throwable) // TODO make side effect
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
                val team = profileState.teams.first { it.id == event.value?.toInt() }
                onTeamSelect(profileState.user, team)
            }
        }) {
            profileState.teams.forEach {
                Option(it.id.toString(), {
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
            if (it != null) {
                Span({
                    style { color(Brand.colors.red) }
                }) {
                    Text(it.description)
                    Text(" ⏱ ")
                    Text("${it.duration}s since ${it.at}")
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
