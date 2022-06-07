# Presentation

## Demo "My Perfect Browser Startpage"

- most important links
- link lists [start.me](start.me)
- multi-search bar
- **task planner**
    * 👆 open [ClickUp](clickup.com)
    * 👆 open task, e.g. `Contributor-Shulung`
        * 👆 change meta information
        * 👆 wait for updates
    * 👆 Pomodoro method
        * 👆 start 10s Pomodoro
        * 👆 show updates stats on [ClickUp](clickup.com)
        * 👆 close task
    * 👆 create task "Präsentation halten"
        * 👆 start task

## Architecture

### Language

- [Kotlin/JS](https://kotlinlang.org/docs/js-overview.html)
    - all Kotlin language features
    - transpiled to JS
- [Kommons](https://github.com/bkahlert/kommons)
    - Kotlin MPP library
    - mainly used for:
        - improved logging
        - color manipulations (change hue, etc.)

### Network

- [Ktor](https://ktor.io/)
  > Ktor is a framework to easily build connected applications – web applications, HTTP services, mobile and browser applications.
    - implementation of server app demonstrated by Fabian
    - used to develop client app
        - Ktor is an MPP library
        - consequently can be used on JVM, natively on Windows, macOS, Android, Linux, etc. and JS (Node and Browser)
- [ClickUp](clickup.com)
    - [REST API](https://clickup.com/api)
    - operations like creation and manipulation of tasks, time tracking, etc.
    - specified with [Blueprint](https://apiblueprint.org/)
        - [OpenAPI](https://www.openapis.org/) alternative
        - bad support for Kotlin client generation (both, Blueprint and OpenAPI)
            - creation of Java client
            - conversion of DTO classes

            * 👆 show [Task](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/api/Task.kt) example

    * 👆 show [ClickUpClient interface](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/api/rest/ClickUpClient.kt)
    * 👆 open [AccessTokenBasedClickUpClient](jetbrains://idea/navigate/reference?project=hello&fqn=AccessTokenBasedClickUpClient)
        * 👆 show `restClient` (i.e. custom auth plugin)
        * 👆 show `getFolder` (i.e. custom auth plugin)
          ```json
          {
              "id": "19413895",
              "name": "Folder Name",
              "hidden": false,
              "space": {
                  "id": "1",
                  "name": "Björn"
              },
              "task_count": "9",
              ...
              "permission_level": "create"
          }
          ```
        * 👆 show `getFolders` (i.e. custom auth plugin)
          ```json
          {
              "folders": [
                  {
                      "id": "19413895",
                      "name": "Folder Name",
                      "hidden": false,
                      "space": {
                          "id": "1",
                          "name": "Björn"
                      },
                      "task_count": "9",
                      ...
                      "permission_level": "create"
                  },
                  ...
                  {
                      ...
                  }
              ]
          }
          ```
        * 👆 open [NamedSerializer](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/kommons/serialization/NamedSerializer.kt)

### UI

- [Compose for Web](https://compose-web.ui.pages.jetbrains.team/)
  > Reactive web UIs for Kotlin, based on Google's modern toolkit and brought to you by JetBrains
    - based on [Jetpack Compose](https://developer.android.com/jetpack/compose)
      > Android’s modern toolkit for building native UI

    * 👆 show/explain [sample](https://compose-web.ui.pages.jetbrains.team/)
    * 👆 show/explain sample 1 at bottom of page
    * 👆 show/explain sample 2 at bottom of page

    - nice builder pattern, i.e. to define stylesheets
    - lack of actual designs, e.g. Material design
    - [Semantic UI](https://semantic-ui.com/)
        - jQuery based framework to develop web UIs
        - comparable to Bootstrap

        * 👆 demonstrate [custom implementation of `Image` binding](http://localhost:8080/#debug=4)
        * 👆 demonstrate [`Image` demo source](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/debug/semanticui/ElementsDemos.kt)

* 👆 show [entrypoint](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/Main.kt)
* 👆 show [ClickUp menu](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/ui/ClickUpMenu.kt)
* 👆 show [refresh](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/ui/ClickUpMenuViewModel.kt)
* 👆 show [startTimeEntry](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/ui/ClickUpMenuViewModel.kt)
    - schedule (menu) state updating lambda
    - using coroutine and last successful state `operation(currentState.lastSucceededState)`
    - `Failed` instance with `Failed.ignore` and `Failed.retry` in case of failure

### Testing

- Tools
    - no [jUnit](https://junit.org/junit5/)
    - no [strikt](https://strikt.io/)
    - instead
        - [kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/)
        - [Kotest Assertions](https://kotest.io/docs/assertions/assertions.html)
        -
            * 👆 show [Kotlin Common Examples](jetbrains://idea/navigate/reference?project=kommons-debug&fqn=com.bkahlert.kommons.CodePointTest)

- Unit Tests
    * 👆 show [Team Serialization Test](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/api/TeamTest.kt)

- Compose Unit Tests
    * 👆 show [ClickUp Menu Test](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/ui/ClickUpMenuTest.kt)

- Visual Tests
    * 👆 show [Debug Mode](http://localhost:8080/#debug)
    * 👆 show [ClickUpTestClient interface](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/debug/clickup/ClickUpTestClient.kt)
    * 👆 show [ClickUp Demo](http://localhost:8080/#debug=1)

## Problems

- Logging
    - lots of data
        * 👆 group made available with [kommons](jetbrains://idea/navigate/reference?project=kommons-debug&path=com/bkahlert/kommons/debug/console.kt)
        * 👆 show logging in [Browser console](http://localhost:8080)
- Deployment
    - custom Gradle plugin
        * 👆 show [usage](jetbrains://idea/navigate/reference?project=hello&path=build.gradle.kts)

        - `./gradlew deploy`
    - Passwords stored in vault
    - Vault decrypted with [password manager integration](https://github.com/bkahlert/ansible-vault-pass-client)
- CORS
    - ClickUp API meant to be integrated by backends
    - Browsers refuse API calls
    - Access proxied with PHP-based [proxy.php](jetbrains://idea/navigate/reference?project=hello&path=proxy.php)

## Try out yourself

- open [hello.bkahlert.com](https://hello.bkahlert.com/#debug=false)
- press `F4`
