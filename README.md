![Hello! — Logo](docs/hello-header.svg)

# Hello! <!--[![Download from Maven Central](https://img.shields.io/maven-central/v/com.bkahlert.hello/web-app?color=FFD726&label=Maven%20Central&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBHZW5lcmF0b3I6IEFkb2JlIElsbHVzdHJhdG9yIDI1LjEuMCwgU1ZHIEV4cG9ydCBQbHVnLUluIC4gU1ZHIFZlcnNpb246IDYuMDAgQnVpbGQgMCkgIC0tPgo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IgoJIHZpZXdCb3g9IjAgMCA1MTIgNTEyIiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA1MTIgNTEyOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI%2BCjxnPgoJPGRlZnM%2BCgkJPHBhdGggaWQ9IlNWR0lEXzFfIiBkPSJNMTAxLjcsMzQ1LjJWMTY3TDI1Niw3Ny45TDQxMC40LDE2N3YxNzguMkwyNTYsNDM0LjNMMTAxLjcsMzQ1LjJ6IE0yNTYsNkwzOS42LDEzMS4ydjI0OS45TDI1Niw1MDYKCQkJbDIxNi40LTEyNC45VjEzMS4yTDI1Niw2eiIvPgoJPC9kZWZzPgoJPHVzZSB4bGluazpocmVmPSIjU1ZHSURfMV8iICBzdHlsZT0ib3ZlcmZsb3c6dmlzaWJsZTtmaWxsOiNGRkZGRkY7Ii8%2BCgk8Y2xpcFBhdGggaWQ9IlNWR0lEXzJfIj4KCQk8dXNlIHhsaW5rOmhyZWY9IiNTVkdJRF8xXyIgIHN0eWxlPSJvdmVyZmxvdzp2aXNpYmxlOyIvPgoJPC9jbGlwUGF0aD4KPC9nPgo8L3N2Zz4K)](https://search.maven.org/search?q=g:com.bkahlert.hello%20AND%20a:web-app)--> <!--[![Download from GitHub Packages](https://img.shields.io/github/v/release/bkahlert/hello?color=69B745&label=GitHub&logo=GitHub&logoColor=fff&style=round)](https://github.com/bkahlert/hello/releases/latest)--> <!--[![Download from Bintray JCenter](https://img.shields.io/bintray/v/bkahlert/koodies/koodies?color=69B745&label=Bintray%20JCenter&logo=JFrog-Bintray&logoColor=fff&style=round)](https://bintray.com/bkahlert/koodies/koodies/_latestVersion)--> <!--[![Build Status](https://img.shields.io/github/actions/workflow/status/bkahlert/hello/build.yml?label=Build&logo=github&logoColor=fff)](https://github.com/bkahlert/hello/actions/workflows/build.yml)--> [![Repository Size](https://img.shields.io/github/repo-size/bkahlert/hello?color=01818F&label=Repo%20Size&logo=Git&logoColor=fff)](https://github.com/bkahlert/hello) [![Repository Size](https://img.shields.io/github/license/bkahlert/hello?color=29ABE2&label=License&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA1OTAgNTkwIiAgeG1sbnM6dj0iaHR0cHM6Ly92ZWN0YS5pby9uYW5vIj48cGF0aCBkPSJNMzI4LjcgMzk1LjhjNDAuMy0xNSA2MS40LTQzLjggNjEuNC05My40UzM0OC4zIDIwOSAyOTYgMjA4LjljLTU1LjEtLjEtOTYuOCA0My42LTk2LjEgOTMuNXMyNC40IDgzIDYyLjQgOTQuOUwxOTUgNTYzQzEwNC44IDUzOS43IDEzLjIgNDMzLjMgMTMuMiAzMDIuNCAxMy4yIDE0Ny4zIDEzNy44IDIxLjUgMjk0IDIxLjVzMjgyLjggMTI1LjcgMjgyLjggMjgwLjhjMCAxMzMtOTAuOCAyMzcuOS0xODIuOSAyNjEuMWwtNjUuMi0xNjcuNnoiIGZpbGw9IiNmZmYiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSIxOS4yMTIiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48L3N2Zz4%3D)](https://github.com/bkahlert/hello/blob/master/LICENSE)

## About

**Hello!** is the personalized homepage for [hello.bkahlert.com][hello-web] consisting of the following subprojects:

### Infrastructure

- [Platforms](platforms) … dependency management
- [Build Logic](build-logic) … Gradle plugins for various project types

### Backend

- [AWS CDK](aws-cdk) … to automatically deploy to Amazon Web Services
- [AWS Lambdas](aws-lambdas) … backend

### Libraries

- [Kommons Libs](libs/kommons-libs) … existing or new [Kommons][kommons] libraries
- [Semantic UI Libs](libs/semantic-ui-libs) … integration of [Semantic UI][semantic-ui] in
  jetBrains [Compose for Web][compose-for-web]

### Web applications

- [Test app](apps/test-app) … minimal [compose][compose-for-web] web application
- [API client](apps/api-client) … playground
- [Web app](apps/web-app) … Hello! web application

The Gradle project at the root of this repository is an umbrella project
that includes all other projects as [included builds][gradle-include-build].

## Cheat sheet

### Re-build everything

Gradle's `base` plugin is applied to every included build
and the umbrella project.
Therefor the tasks `assemble`, `build`, `clean`, and `check`
work as expect—also in the umbrella project.

```shell
./gradlew clean build
```

### Update Gradle wrapper

As this is a composite build all Gradle wrappers should be updated.

Otherwise tasks started from inside an included build might find plugins
previously built by another Gradle wrapper with a different version.
This can lead to problems if the two Gradle version make use of a different
Kotlin DSL.

```shell
./gradle-deep wrapper --gradle-version 8.0-rc-1 
```

> 💡`gradle-deep` can be found at the root of this repo. It invokes `gradle`
> with the same passed arguments in each root project directory.

## Contributing

Want to contribute?
Awesome!
The most basic way to show your support is to star the project or to raise issues.
You can also support this project by making a [PayPal donation](https://www.paypal.me/bkahlert) to ensure this journey continues indefinitely!

Thanks again for your support, it's much appreciated! :pray:

## License

MIT. See [LICENSE](LICENSE) for more details.

## References

[compose]: https://developer.android.com/jetpack/compose (Jetpack Compose—Android’s recommended modern toolkit for building native UI)

[compose-for-web]: https://compose-web.ui.pages.jetbrains.team (Compose for Web—Reactive web UIs for Kotlin, based on Google's modern toolkit and brought to you by JetBrains)

[gradle-include-build]: https://docs.gradle.org/current/userguide/composite_builds.html (Gradle—Composing builds)

[kommons]: https://github.com/bkahlert/kommons (Kommons — Features for Kotlin™ You Didn't Know You Were Missing)

[hello-web]: https://hello.bkahlert.com

[semantic-ui]: https://semantic-ui.com (Semantic UI—User Interface is the language of the web)
