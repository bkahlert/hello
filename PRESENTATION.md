# Presentation

## Demo "My Perfect Browser Startpage"

- most important links
- link lists [start.me](start.me)
- multi-search bar
- **task planner**
    * π open [ClickUp](clickup.com)
    * π open task, e.g. `Contributor-Shulung`
        * π change meta information
        * π wait for updates
    * π Pomodoro method
        * π start 10s Pomodoro
        * π show updates stats on [ClickUp](clickup.com)
        * π close task
    * π create task "PrΓ€sentation halten"
        * π start task

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
  > Ktor is a framework to easily build connected applications β web applications, HTTP services, mobile and browser applications.
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

            * π show [Task](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/api/Task.kt) example

    * π show [ClickUpClient interface](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/api/rest/ClickUpClient.kt)
    * π open [AccessTokenBasedClickUpClient](jetbrains://idea/navigate/reference?project=hello&fqn=AccessTokenBasedClickUpClient)
        * π show `restClient` (i.e. custom auth plugin)
        * π show `getFolder` (i.e. custom auth plugin)
          ```json
          {
              "id": "19413895",
              "name": "Folder Name",
              "hidden": false,
              "space": {
                  "id": "1",
                  "name": "BjΓΆrn"
              },
              "task_count": "9",
              ...
              "permission_level": "create"
          }
          ```
        * π show `getFolders` (i.e. custom auth plugin)
          ```json
          {
              "folders": [
                  {
                      "id": "19413895",
                      "name": "Folder Name",
                      "hidden": false,
                      "space": {
                          "id": "1",
                          "name": "BjΓΆrn"
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
        * π open [NamedSerializer](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/kommons/serialization/NamedSerializer.kt)

### UI

- [Compose for Web](https://compose-web.ui.pages.jetbrains.team/)
  > Reactive web UIs for Kotlin, based on Google's modern toolkit and brought to you by JetBrains
    - based on [Jetpack Compose](https://developer.android.com/jetpack/compose)
      > Androidβs modern toolkit for building native UI

    * π show/explain [sample](https://compose-web.ui.pages.jetbrains.team/)
    * π show/explain sample 1 at bottom of page
    * π show/explain sample 2 at bottom of page

    - nice builder pattern, i.e. to define stylesheets
    - lack of actual designs, e.g. Material design
    - [Semantic UI](https://semantic-ui.com/)
        - jQuery based framework to develop web UIs
        - comparable to Bootstrap

        * π demonstrate [custom implementation of `Image` binding](http://localhost:8080/#debug=4)
        * π demonstrate [`Image` demo source](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/debug/semanticui/ElementsDemos.kt)

* π show [entrypoint](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/Main.kt)
* π show [ClickUp menu](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/ui/ClickUpMenu.kt)
* π show [refresh](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/ui/ClickUpMenuViewModel.kt)
* π show [startTimeEntry](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/ui/ClickUpMenuViewModel.kt)
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
            * π show [Kotlin Common Examples](jetbrains://idea/navigate/reference?project=kommons-debug&fqn=com.bkahlert.kommons.CodePointTest)

- Unit Tests
    * π show [Team Serialization Test](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/api/TeamTest.kt)

- Compose Unit Tests
    * π show [ClickUp Menu Test](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/clickup/ui/ClickUpMenuTest.kt)

- Visual Tests
    * π show [Debug Mode](http://localhost:8080/#debug)
    * π show [ClickUpTestClient interface](jetbrains://idea/navigate/reference?project=hello&path=com/bkahlert/hello/debug/clickup/ClickUpTestClient.kt)
    * π show [ClickUp Demo](http://localhost:8080/#debug=1)

## Problems

- Logging
    - lots of data
        * π group made available with [kommons](jetbrains://idea/navigate/reference?project=kommons-debug&path=com/bkahlert/kommons/debug/console.kt)
        * π show logging in [Browser console](http://localhost:8080)
- Deployment
    - custom Gradle plugin
        * π show [usage](jetbrains://idea/navigate/reference?project=hello&path=build.gradle.kts)

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
