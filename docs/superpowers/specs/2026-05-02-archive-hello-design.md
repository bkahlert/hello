# Archive Hello — design

Branch: `feat/archive`. Goal: land the `hello` repo in a state where both
surviving apps (`apps/web-app`, `apps/playground-app`) boot, render, and
behave correctly without any AWS infrastructure or live OIDC auth, and ship
a Dockerfile that serves them from a single image.

ClickUp resurrection is **out of scope**; it gets its own brainstorm/spec/plan
cycle in a follow-up session.

## Goal & scope

### In scope

- Replace live OIDC session resolution with `FakeSession.Authorized` in
  `AppStore`.
- Route both branches of `AppStore.propsProvider` to `StoragePropsDataSource`
  (no more `RemotePropsDataSource`).
- Drop the `com.bkahlert.aws.app` Gradle plugin from `apps/web-app` and
  `apps/playground-app`; reduce each app's committed `environment.json` to
  `{}`.
- Delete `aws-cdk/`, `aws-lambdas/`, `build-logic/aws/`, and
  `platforms/aws-platform/`, plus their `includeBuild`/`include` registrations
  and any AWS-only entries in `gradle/libs.versions.toml`.
- Trim outdated AWS-specific sections from `CLAUDE.md` (Deploying to prod,
  AWS environments, AWS CLI quirks, environment.json). Replace with a short
  "Building & running" section.
- Add a single-stage `Dockerfile` (nginx:alpine), an `nginx.conf` snippet,
  a `.dockerignore`, and an executable `build` script at the repo root that
  produces both app distributions and the docker image.

### Out of scope (this session)

- ClickUp resurrection — deferred. The **Archival cut surface** section in
  `CLAUDE.md` stays in place as a reference for that follow-up session.
- Tearing down the deployed AWS stacks in the prod account — manual operator
  action, not a code change.
- Multi-stage Dockerfile that builds the apps inside the container — deferred
  until a CI need arises. The `build` script handles the host build, the
  Dockerfile only ships pre-built artifacts.
- Any change to `libs/hello-libs/*`, `libs/kommons-libs/*`, `apps/test-app`.
  Cut surface confirms none of these reference AWS code.

### Success criteria

- `./gradlew build` succeeds with no `software.amazon.*` / `aws.sdk.*`
  references in surviving sources.
- `./gradlew :web-app:jsBrowserDistribution :playground-app:jsBrowserDistribution`
  produces both dists.
- `git grep -E "com\.bkahlert\.aws|software\.amazon\.|aws\.sdk\."` matches
  only the **Archival cut surface** section in `CLAUDE.md`.
- `./build` succeeds end-to-end: gradle dists + `docker build -t hello-archive:latest .`.
- `docker run --rm -p 8080:8080 hello-archive:latest` serves:
  - `GET /` → 200, web-app.
  - `GET /playground` → 301 → `/playground/`.
  - `GET /playground/` → 200, playground-app.
- App boots into authorized state with `JohnDoeInfo`; props persist to
  `localStorage` across reload; QuickLink to `/playground` reaches the
  playground app.

## Branch & commit strategy

Single `feat/archive` branch with four sequential commits, opened as one PR
to `main` at the end. Each commit must leave the build green at its
boundary. No commits on `main`/`master`.

## Commit 1 — auth & props seam (atomic)

One file, two coordinated changes, in
`libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/AppStore.kt`:

1. **`sessionResolver`** (currently lines 33-40) becomes:

   ```kotlin
   private val sessionResolver: suspend (Environment) -> (suspend () -> Session) = {
       { FakeSession.Authorized() }
   },
   ```

   The `Environment` parameter is retained on the function signature
   (preserves the public API) but ignored. Imports removed:
   `AuthorizationCodeFlowState`, `kommons.dom.uri`, `kommons.uri.resolve`,
   `kotlinx.browser.window`. Import added: `FakeSession` from
   `com.bkahlert.hello.app.session`.

2. **`propsProvider`** (currently lines 41-46) collapses to:

   ```kotlin
   private val propsProvider: suspend (Environment, Session) -> PropsDataSource = { _, _ ->
       StoragePropsDataSource(localStorage)
   },
   ```

   Import removed: `RemotePropsDataSource`.

`RemotePropsDataSource.kt` is deleted in the same commit. It was the only
remaining reader of `USER_PROPS_API_ENDPOINT` from `environment.json`; with
the seam closed and the file deleted, `Environment.search` has no live
callers.

### Why atomic

If we flip only `sessionResolver`, the app boots with a fake authorized
session, `propsProvider` routes it to `RemotePropsDataSource`, and the
runtime calls a now-bogus endpoint. Compiler is happy; runtime is broken.
Combining the two changes keeps both build *and* runtime green at the
commit boundary.

### Verification

- `./gradlew build` green.
- `./gradlew :web-app:jsBrowserDistribution`, served via
  `python3 -m http.server` from
  `apps/web-app/build/distributions/`, boots into an authorized state showing
  `JohnDoeInfo`.
- The sign-out button flips the UI to a logged-out state (after the standard
  `DEMO_BASE_DELAY` pause); the sign-in button flips it back. No browser
  redirects, no network calls. After page reload the app is authorized again
  — fresh-resolve semantics, intentional for the archive demo.
- Reload preserves widget props in `localStorage`.
- Playground app: same check, served from
  `apps/playground-app/build/distributions/`.

## Commit 2 — drop aws.app plugin; empty environment.json

Files:

- `apps/web-app/build.gradle.kts` — remove `id("com.bkahlert.aws.app")`
  from `plugins {}`; delete any `aws { … }` extension block; remove
  `DownloadEnvironment`-task wiring if declared inline.
- `apps/playground-app/build.gradle.kts` — same.
- `apps/web-app/src/jsMain/resources/environment.json` — replace with `{}`.
- `apps/playground-app/src/jsMain/resources/environment.json` — replace with `{}`.

`Environment.kt` itself stays untouched. With the file reduced to `{}`, the
loader runs and produces an empty map; no live `Environment.search` calls
remain after Commit 1, so behaviour is unchanged.

### Verification

- `./gradlew :web-app:jsBrowserDistribution :playground-app:jsBrowserDistribution`
  green.
- Both apps boot in a browser; QuickLinks render; props persist; no console
  errors referencing missing env keys.

## Commit 3 — delete AWS modules and registrations

### Trees deleted

- `aws-cdk/` (entire composite-build root).
- `aws-lambdas/` (entire composite-build root, includes `userinfo-api-handlers`,
  `userprops-api-handlers`, the unused `clickup-api-handlers`, and `minimal-base`).
- `build-logic/aws/` (subproject of the `build-logic` composite — `build-logic`
  itself stays; only its `aws` subproject goes).
- `platforms/aws-platform/` (subproject of the `platforms` composite — `platforms`
  itself stays; only the `aws-platform` subproject goes).

### Edits

- `settings.gradle.kts` — remove `includeBuild("aws-cdk")` and
  `includeBuild("aws-lambdas")`.
- `build-logic/settings.gradle.kts` — remove the `include(":aws")` (or
  equivalent) registration.
- `platforms/settings.gradle.kts` — remove the `include(":aws-platform")`
  registration.
- `gradle/libs.versions.toml` — remove entries that exist solely for the
  deleted modules (CDK, AWS SDK, Lambda runtime). Shared catalog entries
  stay.
- `CLAUDE.md` — remove the **Deploying to prod**, **AWS environments**,
  **AWS CLI quirks**, and **environment.json** sections. Keep **Build
  prerequisites**, **Composite build layout**, **Static assets in Kotlin/JS**,
  and **Archival cut surface**. (The "Building & running" section is added
  in Commit 4.)

### Risk: orphan dependencies

A surviving module might still declare a `project(":aws-cdk:something")`
dependency. The cut surface in `CLAUDE.md` says no surviving libs import
AWS, so confidence is high. If it happens anyway, `./gradlew build` fails
fast and the fix is to widen Commit 1's scope (i.e., delete the offending
reference at its source).

### Verification

- `./gradlew build` green.
- `git grep -E "com\.bkahlert\.aws|software\.amazon\.|aws\.sdk\."` matches
  only the **Archival cut surface** section in `CLAUDE.md`.

## Commit 4 — Dockerfile, nginx.conf, build script

### Files added

`Dockerfile` (repo root):

```dockerfile
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY apps/web-app/build/distributions/         /usr/share/nginx/html/
COPY apps/playground-app/build/distributions/  /usr/share/nginx/html/playground/
EXPOSE 8080
```

`nginx.conf` (repo root):

```nginx
server {
    listen 8080;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;
    autoindex off;

    location = /playground { return 301 /playground/; }

    location /playground/ { try_files $uri $uri/ =404; }
    location / { try_files $uri $uri/ =404; }
}
```

No SPA fallback. Both apps use fritz2's `Router<T>`, which is hash-based
(`PageRouter.deserialize(hash: String)` confirms it); deep links live in
the URL fragment, never the path, so the static server never sees the
route fragment.

`.dockerignore` (repo root):

Minimal — exclude `**/build/` *except* the two `distributions/` directories
that the Dockerfile explicitly copies. Without it, `docker build` slurps
the entire build cache into context.

`build` (repo root, executable, no extension):

```bash
#!/usr/bin/env bash
set -euo pipefail

export JAVA_HOME='/Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19'
[[ -d $JAVA_HOME ]] || { echo "JAVA_HOME not found: $JAVA_HOME — install Liberica 11.0.19" >&2; exit 1; }

cd "$(dirname "$0")"

./gradlew \
  :web-app:jsBrowserDistribution \
  :playground-app:jsBrowserDistribution

docker build -t hello-archive:latest .

cat <<EOF

✓ Built hello-archive:latest

Run:   docker run --rm -p 8080:8080 hello-archive:latest
Open:  http://localhost:8080/             (web-app)
       http://localhost:8080/playground/  (playground)
EOF
```

`set -euo pipefail` aborts on first failure. The JDK guard is the one
non-obvious prerequisite worth a loud error. `cd "$(dirname "$0")"` lets
the script run from any working directory.

### CLAUDE.md addition

Add a short subsection (replacing the AWS-related sections trimmed in
Commit 3):

> ## Building & running
>
> `./build` builds both apps and the Docker image. Run with
> `docker run --rm -p 8080:8080 hello-archive:latest`.

### Verification

- `./build` end-to-end green from a fresh `git clean -fdx` checkout.
- `docker run --rm -p 8080:8080 hello-archive:latest`:
  - `curl -sI http://localhost:8080/` → 200, `text/html`.
  - `curl -sI http://localhost:8080/playground` → 301, `Location: /playground/`.
  - `curl -sI http://localhost:8080/playground/` → 200, `text/html`.
  - `curl -sI http://localhost:8080/web-app.js` → 200.
  - `curl -sI http://localhost:8080/playground/playground-app.js` → 200.
- Browser visit: web-app boots authorized, QuickLink-to-`/playground`
  works, playground app boots and renders nav.

## Hard constraints

- Never commit on `main`/`master`.
- JDK 11 (Liberica 11.0.19) required:
  `JAVA_HOME=/Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19`.
- Composite build — task paths are like
  `:aws-lambdas:userinfo-api-handlers:shadowJar`, not the bare module name.
- Don't push, don't open PRs without explicit go-ahead from the user.
