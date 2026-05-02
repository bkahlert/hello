# Hello — agent guidance

This file captures project-specific context that isn't obvious from reading the code.
For general guidance see the global `~/.config/agents/AGENTS.md`.

## Build prerequisites

- **JDK 11** (Liberica 11.0.19). Newer JDKs break Gradle 8.2.1 with a cryptic `What went wrong: 25.0.2`-style error.
  Set `JAVA_HOME=/Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19` before running anything.
  In IntelliJ: Settings → Build Tools → Gradle → Gradle JVM.

## Composite build layout

Top-level [settings.gradle.kts](settings.gradle.kts) aggregates every module via `includeBuild`. Tasks in
included builds are reached with their composite path, e.g. `:aws-lambdas:userinfo-api-handlers:shadowJar`,
not `:userinfo-api-handlers:shadowJar`.

## Static assets in Kotlin/JS

Files in `src/jsMain/resources/` of any module ship next to `index.html` in the app distribution.
The `kotlin-js-browser-application` convention plugin
([build-logic/kotlin-project/.../com.bkahlert.kotlin-js-browser-application.gradle.kts](build-logic/kotlin-project/src/main/kotlin/com.bkahlert.kotlin-js-browser-application.gradle.kts))
extracts non-metadata files from every `.klib` on the `jsRuntimeClasspath` into the app's `processedResources`,
so library-provided assets (e.g. [nyancat.svg](libs/hello-libs/hello-app/src/jsMain/resources/nyancat.svg))
reach the bundle. Reference them as relative URLs (`Uri("nyancat.svg")`).

## Building & running

### Local

[`./build`](build) builds both Kotlin/JS apps and the Docker image,
tagged `hello:museum`. It auto-detects podman and passes `--format=docker`
so the Dockerfile's `HEALTHCHECK` survives (OCI default strips it).
[`./smoke`](smoke) runs the image and verifies the five known routes
(`/`, `/playground`, `/playground/`, `/web-app.js`,
`/playground/playground-app.js`); it polls HTTP readiness rather than
inspecting `Health.Status` because podman doesn't auto-run healthchecks
without systemd.

```bash
./build && ./smoke
docker run --rm -p 8080:8080 hello:museum
```

Web-app at `/`, playground-app at `/playground/`.

### From Docker Hub

[`.github/workflows/build.yml`](.github/workflows/build.yml) rebuilds
the image on every push to `museum` and on `workflow_dispatch`, publishing
to `docker.io/bkahlert/hello`:

| Tag | Mutability | Purpose |
|---|---|---|
| `bkahlert/hello:museum` | moving | latest museum build |
| `bkahlert/hello:museum-<short-sha>` | immutable | per-commit pin |

Multi-arch (`linux/amd64`, `linux/arm64`), with provenance + SBOM
attestations. No `:latest` — none of the variants is canonical.

```bash
docker run --rm -p 8080:8080 bkahlert/hello:museum
./smoke bkahlert/hello:museum   # verify a registry image
```

CI gradle is split into two per-app invocations with `-Pkotlin.daemon.jvmargs="-Xmx2g -Xss16m"`
to dodge a yarn-cache race in composite builds and a `StackOverflowError`
in `:hello-libs:hello-icon` (~600 delegated `DataUri` properties needs
deep stack); both are CI-only, the project's `gradle.properties` keeps
`org.gradle.parallel=true` so local builds stay fast.

## Archival cut surface

Reference for the in-progress work to remove AWS, remove auth, and re-introduce ClickUp as a mocked widget. Keep this section in sync as the work proceeds; delete it once archival is complete.

### Modules by fate

- **Delete outright**: `aws-cdk/*`, `aws-lambdas/*`, `build-logic/aws`, `platforms/aws-platform`.
- **Surgery (small)**: `apps/web-app`, `apps/playground-app` — drop the `com.bkahlert.aws.app` plugin and the `environment.json` download.
- **Untouched**: `apps/test-app`, all `libs/hello-libs/*`, all `libs/kommons-libs/*`. None import `software.amazon.*` or `aws.sdk.*`.

### Auth seam

The single cut point is the `sessionResolver` parameter in
[AppStore.kt:33-40](libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/AppStore.kt). It currently
calls `AuthorizationCodeFlowState.resolve()` against `PROVIDER_URL`/`CLIENT_ID` from `environment.json`. Replace
the supplier with `FakeSession.Authorized(...)` from
[FakeSession.kt](libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/session/FakeSession.kt) — the
mock impl already exists. The `Session` interface itself lives in `kommons-net` and is generic OpenID, no AWS code.

The `RemotePropsDataSource` path
([RemotePropsDataSource.kt:72-78](libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/props/RemotePropsDataSource.kt))
becomes unreachable once the session is always-mocked, but `propsProvider` in `AppStore.kt:41-46` already falls
back to `StoragePropsDataSource` for unauthorized sessions — point both branches at storage.

### ClickUp resurrection

The last commit with ClickUp code in tree is **`0c50057f^`** (parent of `0c50057f` "chore: remove Semantic UI,
Compose, and ClickUp", 2023-05-26). Three modules existed at that SHA, all under `libs/hello-libs/`:

- `clickup-model` — data model (`Task`, `Team`, `Space`, `Folder`, `TaskList`, `TimeEntry`, `User`,
  `CustomField`, `Status`, `Tag`, `Identifier`), `ClickUpClient` interface, and fixtures (~46 files).
- `clickup-ui-compose` — `ClickUpHttpClient` + Compose-for-Web view layer (~66 files).
- `clickup` — top-level fritz2 integration: `ClickUpMenuElement.kt`, `compose.kt`, `styles.kt`.

Restore all three plus the widget call sites in **both** apps:

```bash
git checkout 0c50057f^ -- \
  libs/hello-libs/clickup-model/ \
  libs/hello-libs/clickup-ui-compose/ \
  libs/hello-libs/clickup/ \
  apps/web-app/src/jsMain/kotlin/com/bkahlert/hello/components/ClickUp.kt \
  apps/playground-app/src/jsMain/kotlin/playground/components/app/clickUpApp.kt
```

**Compose plugin caveat.** The same commit `0c50057f` also deleted the
`com.bkahlert.compose-for-web-project` convention plugin
(`build-logic/kotlin-project/src/main/kotlin/com.bkahlert.compose-for-web-project.gradle.kts`) and the
`compose` version entry in `gradle/libs.versions.toml`. `clickup-ui-compose` and `clickup` both depend on that
plugin. Two options:

- **Restore Compose**: `git checkout 0c50057f^ -- build-logic/kotlin-project/src/main/kotlin/com.bkahlert.compose-for-web-project.gradle.kts gradle/libs.versions.toml` (cherry-pick the compose version line; don't overwrite the whole file). Re-apply the plugin in `libs/hello-libs/clickup-ui-compose/build.gradle.kts` and `libs/hello-libs/clickup/build.gradle.kts`.
- **Rewrite in fritz2**: drop `clickup-ui-compose` + `clickup` and re-implement the widget UI in fritz2 (the convention used by every surviving app/lib). Keep `clickup-model` as-is.

Do **not** restore the deleted `aws-lambdas/clickup-api-handlers/` or `apps/web-app/webpack.config.d/clickup-proxy.js` —
the mocked widget should serve from the fixtures already present in `clickup-model`
(`ClickUpFixtures.kt`, `ClickUpTestClient.kt`).

The orphan `ClickUp` CDK stack in the prod account is unrelated to the resurrection work; it can be destroyed
manually when the rest of `aws-cdk/` is torn down.

### environment.json runtime readers

[Environment.kt:63-74](libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/env/Environment.kt)
loads the file at boot, and `Environment.search()` throws `NoSuchElementException` on missing keys (lines 25-28).
There are exactly three read sites:

- `AppStore.kt:36` → `USER_POOL_PROVIDER_URL` (delete with auth)
- `AppStore.kt:37` → `USER_POOL_CLIENT_ID` (delete with auth)
- `RemotePropsDataSource.kt:76` → `USER_PROPS_API_ENDPOINT` (delete with remote props)

Once those three call sites are gone, `environment.json` can be reduced to `{}` or removed entirely; nothing
else reads it. Currently-listed-but-unread fields: `SITE_URL`, `SITE_BUCKET_NAME`, `DISTRIBUTION_*`,
`SITE_ENVIRONMENT_API_ENDPOINT`, `CLICK_UP_API_ENDPOINT`.
