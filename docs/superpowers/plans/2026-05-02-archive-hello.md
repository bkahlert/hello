# Archive Hello Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Land `feat/archive` so both surviving apps boot, render, and behave correctly without any AWS infrastructure or live OIDC auth, and ship a Dockerfile + build script that serves them from a single nginx:alpine image.

**Architecture:** Four sequential commits on `feat/archive`. Commit 1 closes the auth/props seam atomically (one file edit, one file delete). Commit 2 drops the `com.bkahlert.aws.app` Gradle plugin from both apps and reduces `environment.json` files to `{}`. Commit 3 deletes the four AWS-bearing module trees and trims AWS sections from `CLAUDE.md`. Commit 4 adds Dockerfile/nginx.conf/.dockerignore/build script, fixes the Playground QuickLink to deep-link into the chatbot showcase, and adds a short "Building & running" subsection to `CLAUDE.md`.

**Tech Stack:** Kotlin 1.8.21, Gradle 8.2.1 (composite build), Kotlin/JS browser apps, fritz2, JDK 11 (Liberica 11.0.19), nginx:alpine for the runtime image, bash for the build script.

**Spec:** [`docs/superpowers/specs/2026-05-02-archive-hello-design.md`](../specs/2026-05-02-archive-hello-design.md)

---

## Pre-flight

**Files:** none touched.

- [ ] **Step 1: Confirm branch and clean tree**

```bash
git branch --show-current
git status
```

Expected:
```
feat/archive
On branch feat/archive
nothing to commit, working tree clean
```

If branch is not `feat/archive`, stop and ask the user. Hard constraint from spec: never commit on `main`/`master`. If working tree is dirty, ask before continuing.

- [ ] **Step 2: Confirm JAVA_HOME**

```bash
ls -d /Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19
```

Expected: the directory exists. Export it for the rest of the session:

```bash
export JAVA_HOME=/Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19
```

If it does not exist, stop. Gradle 8.2.1 fails with cryptic errors on newer JDKs.

- [ ] **Step 3: Baseline build (sanity check)**

```bash
./gradlew :web-app:jsBrowserDistribution :playground-app:jsBrowserDistribution
```

Expected: both tasks succeed; `apps/web-app/build/distributions/` and `apps/playground-app/build/distributions/` populated. This proves the starting point is green so any later regression is unambiguously something we caused.

---

## Task 1 — Commit 1: auth & props seam (atomic)

**Goal:** Replace live OIDC session resolution with `FakeSession.Authorized()`; route both `propsProvider` branches to `StoragePropsDataSource`; delete the now-unreachable `RemotePropsDataSource`.

**Files:**
- Modify: `libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/AppStore.kt`
- Delete: `libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/props/RemotePropsDataSource.kt`

**Why atomic:** flipping only `sessionResolver` would leave the runtime broken (fake authorized session would call `RemotePropsDataSource` against a now-bogus endpoint). Compile-green ≠ run-green; this commit must keep both green at its boundary.

**Verification approach:** This codebase has no `jsTest` files for `hello-app`, so there is no test infrastructure to extend. The spec defines verification as `./gradlew build` + manual browser smoke. Plan respects this: build green + browser smoke at the end.

- [ ] **Step 1: Edit `AppStore.kt` — replace `sessionResolver` and `propsProvider`**

Open `libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/AppStore.kt`.

Replace the existing `sessionResolver` parameter (lines 33-40 in the unmodified file) and the `propsProvider` parameter (lines 41-46). The full updated `AppStore` constructor parameter list looks like:

```kotlin
public class AppStore(
    initialData: AppState = AppState.Loading,
    private val environmentProvider: suspend () -> Environment = { Environment.load() },
    private val sessionResolver: suspend (Environment) -> (suspend () -> Session) = {
        { FakeSession.Authorized() }
    },
    private val propsProvider: suspend (Environment, Session) -> PropsDataSource = { _, _ ->
        StoragePropsDataSource(localStorage)
    },
    id: String = Id.next(),
) : RootStore<AppState>(initialData, id) {
```

Then update the import block at the top of the file. Remove these imports (no longer referenced):

```kotlin
import com.bkahlert.hello.app.props.RemotePropsDataSource
import com.bkahlert.kommons.dom.uri
import com.bkahlert.kommons.oauth.AuthorizationCodeFlowState
import com.bkahlert.kommons.uri.resolve
import kotlinx.browser.window
```

Add this import (new reference):

```kotlin
import com.bkahlert.hello.app.session.FakeSession
```

The remaining body of `AppStore` (handlers, `init`, `AppCache` class) is unchanged.

- [ ] **Step 2: Delete `RemotePropsDataSource.kt`**

```bash
rm libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/props/RemotePropsDataSource.kt
```

This was the only remaining reader of `USER_PROPS_API_ENDPOINT` from `environment.json`, and the only consumer of `kommons.ktor.JsonHttpClient` from this module. With it gone, `Environment.search` has no live callers.

- [ ] **Step 3: Verify the build is still green**

```bash
./gradlew build
```

Expected: BUILD SUCCESSFUL. If it fails complaining about unused imports, fix any stragglers in `AppStore.kt` and re-run. If it fails because some other module depended on `RemotePropsDataSource`, the cut surface in `CLAUDE.md` was wrong — stop, report, and widen the scope of this commit to include the offending reference.

- [ ] **Step 4: Verify the web-app distribution boots and round-trips sign-in/out**

```bash
./gradlew :web-app:jsBrowserDistribution
( cd apps/web-app/build/distributions && python3 -m http.server 8000 ) &
HTTP_PID=$!
sleep 1
echo "Open http://localhost:8000/ in a browser; verify:"
echo "  - app boots authorized as JohnDoeInfo (no spinner stuck)"
echo "  - clicking sign-out flips to logged-out (after DEMO_BASE_DELAY pause)"
echo "  - clicking sign-in flips back to logged-in"
echo "  - widget props persist across F5 reload"
echo "  - browser console has no errors mentioning environment.json keys or auth endpoints"
read -r -p "Press Enter once verified... " _
kill $HTTP_PID 2>/dev/null || true
```

Expected: all four checks pass. If any fails, report and stop — this is the load-bearing commit.

- [ ] **Step 5: Verify the playground-app distribution boots**

```bash
./gradlew :playground-app:jsBrowserDistribution
( cd apps/playground-app/build/distributions && python3 -m http.server 8000 ) &
HTTP_PID=$!
sleep 1
echo "Open http://localhost:8000/ in a browser; verify:"
echo "  - playground boots and renders the page nav"
echo "  - browser console has no errors"
read -r -p "Press Enter once verified... " _
kill $HTTP_PID 2>/dev/null || true
```

Expected: both checks pass.

- [ ] **Step 6: Commit**

```bash
git add libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/AppStore.kt
git add libs/hello-libs/hello-app/src/jsMain/kotlin/com/bkahlert/hello/app/props/RemotePropsDataSource.kt
git commit -m "$(cat <<'EOF'
refactor: replace OIDC session and remote props with FakeSession + storage

Closes the AppStore auth/props seam atomically: sessionResolver always
returns FakeSession.Authorized() and propsProvider always returns
StoragePropsDataSource. RemotePropsDataSource is deleted (last reader of
USER_PROPS_API_ENDPOINT). Apps now run as a static, single-user demo
backed by localStorage; sign-in/sign-out round-trips in-memory via
FakeSession.

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>
EOF
)"
git log --oneline -1
```

Expected: `git log -1` shows the new commit.

---

## Task 2 — Commit 2: drop `com.bkahlert.aws.app` plugin; empty `environment.json`

**Goal:** Detach the two apps from the `com.bkahlert.aws.app` convention plugin, which exists solely to wire the `DownloadEnvironment` Gradle task. With Commit 1 closing the seam, every key in `environment.json` is dead; the file is reduced to `{}` (still required by `Environment.load()`, which does an HTTP `expectSuccess = true` GET on `environment.json` at boot — a missing file would 404).

**Files:**
- Modify: `apps/web-app/build.gradle.kts`
- Modify: `apps/playground-app/build.gradle.kts`
- Modify: `apps/web-app/src/jsMain/resources/environment.json`
- Modify: `apps/playground-app/src/jsMain/resources/environment.json`
- Delete: `apps/web-app/environment.json` (project-root copy — was just a CDK output cache, no longer regenerated)
- Delete: `apps/playground-app/environment.json`

**Note on `gradle/libs.versions.toml`:** Verified — zero AWS-related entries. Skip the catalog edit step the spec hedged about.

- [ ] **Step 1: Edit `apps/web-app/build.gradle.kts`**

Replace the `plugins {}` block (drop the last entry):

```kotlin
plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.fritz2-project")
    id("com.bkahlert.tailwindcss-project")
}
```

The rest of the file is unchanged.

- [ ] **Step 2: Edit `apps/playground-app/build.gradle.kts`**

Same edit:

```kotlin
plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.fritz2-project")
    id("com.bkahlert.tailwindcss-project")
}
```

The rest of the file is unchanged.

- [ ] **Step 3: Replace `apps/web-app/src/jsMain/resources/environment.json` with `{}`**

```bash
echo '{}' > apps/web-app/src/jsMain/resources/environment.json
```

- [ ] **Step 4: Replace `apps/playground-app/src/jsMain/resources/environment.json` with `{}`**

```bash
echo '{}' > apps/playground-app/src/jsMain/resources/environment.json
```

- [ ] **Step 5: Delete the project-root `environment.json` files**

```bash
rm apps/web-app/environment.json
rm apps/playground-app/environment.json
```

These were the artifacts the (now-removed) `DownloadEnvironment` task wrote to. They are not bundled into the dist (the bundled copy lives under `src/jsMain/resources/`).

- [ ] **Step 6: Verify the build is still green**

```bash
./gradlew build
```

Expected: BUILD SUCCESSFUL. If Gradle complains about an unknown plugin id `com.bkahlert.aws.app`, the build-logic composite still has the plugin available (we are not deleting `build-logic/aws/` until Task 3) — just no longer applying it. If it fails because a Gradle script in the apps still references an `awsApp { … }` extension or a `downloadEnvironment` task explicitly, edit those references out and re-run.

- [ ] **Step 7: Verify both dists boot in a browser**

```bash
./gradlew :web-app:jsBrowserDistribution :playground-app:jsBrowserDistribution
```

Then for each app, in turn:

```bash
( cd apps/web-app/build/distributions && python3 -m http.server 8000 ) &
HTTP_PID=$!
sleep 1
echo "Open http://localhost:8000/ — verify the app boots, no console errors mentioning missing env keys."
read -r -p "Press Enter once verified... " _
kill $HTTP_PID 2>/dev/null || true

( cd apps/playground-app/build/distributions && python3 -m http.server 8000 ) &
HTTP_PID=$!
sleep 1
echo "Open http://localhost:8000/ — verify the playground boots, no console errors."
read -r -p "Press Enter once verified... " _
kill $HTTP_PID 2>/dev/null || true
```

Expected: both apps boot, no env-key errors, props persist across reload.

- [ ] **Step 8: Commit**

```bash
git add apps/web-app/build.gradle.kts apps/playground-app/build.gradle.kts
git add apps/web-app/src/jsMain/resources/environment.json
git add apps/playground-app/src/jsMain/resources/environment.json
git add -u apps/web-app/environment.json apps/playground-app/environment.json
git commit -m "$(cat <<'EOF'
chore: drop com.bkahlert.aws.app plugin from web-app and playground-app

The plugin's only job was wiring the DownloadEnvironment Gradle task,
which generates environment.json from CDK stack outputs. With the
AppStore seam closed in the previous commit, every key in that file is
dead, so the bundled environment.json files are reduced to {} (kept
because Environment.load() does an HTTP GET with expectSuccess) and the
project-root CDK-output copies are deleted.

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>
EOF
)"
git log --oneline -1
```

Expected: `git log -1` shows the new commit.

---

## Task 3 — Commit 3: delete AWS module trees; trim CLAUDE.md

**Goal:** Remove the four AWS-bearing module trees and their composite-build registrations, and delete the four `CLAUDE.md` sections that describe AWS infrastructure that no longer exists.

**Files:**
- Delete: `aws-cdk/` (entire tree)
- Delete: `aws-lambdas/` (entire tree)
- Delete: `build-logic/aws/` (subdirectory of build-logic composite)
- Delete: `platforms/aws-platform/` (subdirectory of platforms composite)
- Modify: `settings.gradle.kts` (remove two `includeBuild` lines)
- Modify: `CLAUDE.md` (delete four sections)

**Notes:**
- Both `build-logic/settings.gradle.kts` and `platforms/settings.gradle.kts` auto-include subdirectories that contain a `build.gradle.kts` — no `include()` line edits needed; deleting the directories is sufficient.
- `cdk.context.json` lives at `aws-cdk/app/cdk.context.json` and gets removed with the `aws-cdk/` tree — no separate handling.
- The `Archival cut surface` section of `CLAUDE.md` stays (kept for the deferred ClickUp follow-up session).

- [ ] **Step 1: Edit `settings.gradle.kts` — remove the AWS `includeBuild` lines**

The full file after the edit:

```kotlin
includeBuild("platforms")
includeBuild("build-logic")
includeBuild("libs/kommons-libs")
includeBuild("libs/hello-libs")
includeBuild("apps/playground-app")
includeBuild("apps/test-app")
includeBuild("apps/web-app")
```

Two lines removed: `includeBuild("aws-cdk")` and `includeBuild("aws-lambdas")`.

- [ ] **Step 2: Delete the four module trees**

```bash
rm -rf aws-cdk aws-lambdas build-logic/aws platforms/aws-platform
```

- [ ] **Step 3: Edit `CLAUDE.md` — delete four sections**

Open `CLAUDE.md`. Delete the entirety of these four sections (lines start at the `##` headers, end at the line before the next `##`):

- `## Deploying to prod`
- `## AWS environments`
- `## AWS CLI quirks`
- `## environment.json`

Keep all other sections intact, including `## Archival cut surface` (and its subsections). The new "Building & running" section is added in Task 4 — not in this commit.

After this edit, the file's `##`-level outline is:
```
## Build prerequisites
## Composite build layout
## Static assets in Kotlin/JS
## Archival cut surface
```

- [ ] **Step 4: Verify the build is still green**

```bash
./gradlew build
```

Expected: BUILD SUCCESSFUL. If a surviving module declares a `project(":aws-cdk:…")` or `project(":aws-lambdas:…")` dependency, Gradle will fail with an unresolved-project error — the cut surface said no surviving libs reference AWS, but if it lied, fix the offending reference at its source and re-run.

- [ ] **Step 5: Verify no stray AWS references remain in code or build**

```bash
git grep -nE 'com\.bkahlert\.aws|software\.amazon\.|aws\.sdk\.'
```

Expected: matches **only** inside the `## Archival cut surface` section of `CLAUDE.md`. Any other match is a leak — stop and investigate.

- [ ] **Step 6: Verify both dists still boot**

```bash
./gradlew :web-app:jsBrowserDistribution :playground-app:jsBrowserDistribution
```

Expected: both succeed. (Skip the manual browser check this time — Commit 3 deletes infrastructure that no live code path used; if Commit 2 worked, Commit 3 can't have broken runtime behavior.)

- [ ] **Step 7: Commit**

```bash
git add settings.gradle.kts CLAUDE.md
git add -u aws-cdk aws-lambdas build-logic/aws platforms/aws-platform
git commit -m "$(cat <<'EOF'
chore: remove aws-cdk, aws-lambdas, build-logic/aws, platforms/aws-platform

The AWS infrastructure trees and their composite-build registrations are
deleted, along with four CLAUDE.md sections describing infrastructure
that no longer exists (Deploying to prod, AWS environments, AWS CLI
quirks, environment.json). The Archival cut surface section is kept for
the deferred ClickUp follow-up session.

The deployed AWS stacks in the prod account are unaffected — that is a
manual operator action, not a code change.

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>
EOF
)"
git log --oneline -1
```

Expected: `git log -1` shows the new commit.

---

## Task 4 — Commit 4: Dockerfile, nginx.conf, .dockerignore, build script, QuickLink fix, CLAUDE.md addition

**Goal:** Add the runtime image and the one-command build script. Fix the Playground QuickLink to deep-link into the chatbot showcase in the playground app.

**Files:**
- Create: `Dockerfile`
- Create: `nginx.conf`
- Create: `.dockerignore`
- Create: `build` (executable, no extension)
- Modify: `libs/hello-libs/hello-quick-links/src/jsMain/kotlin/com/bkahlert/hello/quicklink/QuickLinks.kt`
- Modify: `CLAUDE.md`

- [ ] **Step 1: Edit `QuickLinks.kt:219` — deep-link the Playground bookmark**

In the `DefaultLinks` companion list, the third entry's `url` becomes:

```kotlin
url = Uri("/playground/#widgets/chatbot-widget"),
```

The `title` ("Playground") and `icon` are unchanged.

Reason: the web-app's `WidgetRouter` deserialises hashes as `<widget-id>[/edit]`, so a bare `#widgets/chatbot-widget` would not navigate to a widget there. The two-segment hash is a playground-app `PageRouter` path (parent `widgets`, child `chatbot-widget`). The href combines path (`/playground/` — picked up by nginx, serves the playground app) and hash (`#widgets/chatbot-widget` — deserialised by the playground's `PageRouter`).

- [ ] **Step 2: Create `Dockerfile` at repo root**

```dockerfile
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY apps/web-app/build/distributions/         /usr/share/nginx/html/
COPY apps/playground-app/build/distributions/  /usr/share/nginx/html/playground/
EXPOSE 8080
```

`nginx:alpine` runs the nginx daemon as its default `CMD` on port 80; our config overrides to 8080 (so the container can run unprivileged). No extra `CMD` line needed.

- [ ] **Step 3: Create `nginx.conf` at repo root**

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

No SPA fallback. Both apps' fritz2 routers are hash-based (`PageRouter.deserialize(hash: String)`), so deep links live in the URL fragment — the server never sees the route.

- [ ] **Step 4: Create `.dockerignore` at repo root**

```gitignore
# Keep the build context small. The Dockerfile only needs the two dist
# directories and the nginx.conf alongside the Dockerfile itself.

**
!nginx.conf
!apps/web-app/build/distributions/**
!apps/playground-app/build/distributions/**
```

The `**` excludes everything by default; the `!` lines re-include only what the Dockerfile copies. Without this, `docker build` slurps the entire `.gradle/` cache, every `node_modules`, every `build/` output across the composite — minutes of context upload.

- [ ] **Step 5: Create the `build` script at repo root (no extension)**

```bash
#!/usr/bin/env bash
#
# build — produce both Kotlin/JS distributions and the hello-archive
#         Docker image.
#
# Usage:
#   ./build
#
# Prerequisites:
#   - JDK 11 (Liberica 11.0.19) installed at the path below
#   - docker daemon running

set -euo pipefail

green=$(tput setaf 2 2>/dev/null || true)
red=$(tput setaf 1 2>/dev/null || true)
reset=$(tput sgr0 2>/dev/null || true)

export JAVA_HOME='/Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19'
if [[ ! -d $JAVA_HOME ]]; then
    printf '%s✘%s JAVA_HOME not found: %s — install Liberica 11.0.19\n' \
        "$red" "$reset" "$JAVA_HOME" >&2
    exit 1
fi

cd "$(dirname "$0")"

./gradlew \
    :web-app:jsBrowserDistribution \
    :playground-app:jsBrowserDistribution

docker build -t hello-archive:latest .

printf '\n%s✔%s Built hello-archive:latest\n\n' "$green" "$reset"
printf 'Run:   docker run --rm -p 8080:8080 hello-archive:latest\n'
printf 'Open:  http://localhost:8080/             (web-app)\n'
printf '       http://localhost:8080/playground/  (playground)\n'
```

Then make it executable:

```bash
chmod +x build
```

Style notes (per `~/.config/agents/rules/bash.md`):
- Shebang `#!/usr/bin/env bash`. Yes.
- No file extension. Yes (`build`, not `build.sh`).
- Executable. Yes (`chmod +x`).
- Header comment with purpose & usage. Yes.
- No hardcoded escape sequences — uses `tput`. Yes.
- Icons from the project palette (`✔` success, `✘` error). Yes.

- [ ] **Step 6: Edit `CLAUDE.md` — add the "Building & running" section**

Add this section in place of the four AWS sections deleted in Task 3 (so it sits between `## Static assets in Kotlin/JS` and `## Archival cut surface`):

```markdown
## Building & running

`./build` builds both apps and the Docker image. Run with
`docker run --rm -p 8080:8080 hello-archive:latest`. Web-app at
`/`, playground-app at `/playground/`.
```

- [ ] **Step 7: Run `./build` end-to-end**

```bash
./build
```

Expected:
- Gradle reports `BUILD SUCCESSFUL` on both `:web-app:jsBrowserDistribution` and `:playground-app:jsBrowserDistribution`.
- `docker build` succeeds, ending with `Successfully tagged hello-archive:latest`.
- Final stanza prints the green `✔ Built hello-archive:latest` line and the run/open URLs.

If `tput` is unavailable (e.g., non-interactive shell), the script still runs — the colour vars fall back to empty strings.

- [ ] **Step 8: Run the container and verify routes via curl**

```bash
docker run --rm -d -p 8080:8080 --name hello-archive-test hello-archive:latest
sleep 1

curl -sI http://localhost:8080/ | head -1
curl -sI http://localhost:8080/playground | head -2
curl -sI http://localhost:8080/playground/ | head -1
curl -sI http://localhost:8080/web-app.js | head -1
curl -sI http://localhost:8080/playground/playground-app.js | head -1

docker stop hello-archive-test
```

Expected output:

```
HTTP/1.1 200 OK
HTTP/1.1 301 Moved Permanently
Location: /playground/
HTTP/1.1 200 OK
HTTP/1.1 200 OK
HTTP/1.1 200 OK
```

If any line is wrong, `docker stop` first, then debug — most likely culprit is `nginx.conf` indentation or a typo in the `location` block.

- [ ] **Step 9: Browser smoke — full archive flow**

```bash
docker run --rm -d -p 8080:8080 --name hello-archive-test hello-archive:latest
sleep 1
echo "Open http://localhost:8080/ in a browser; verify:"
echo "  - web-app boots authorized as JohnDoeInfo"
echo "  - clicking the Playground QuickLink lands on"
echo "    http://localhost:8080/playground/#widgets/chatbot-widget"
echo "    showing the chatbot widget showcase (not a 404, not the playground home)"
echo "  - browser back-button returns to the web-app with state intact"
echo "  - F5 on either app preserves widget props from localStorage"
read -r -p "Press Enter once verified... " _
docker stop hello-archive-test
```

Expected: all checks pass. If the QuickLink lands on the playground home (no `#widgets/chatbot-widget` selection), check that `QuickLinks.kt:219` was edited correctly and that the new dist was rebuilt before `docker build`.

- [ ] **Step 10: Commit**

```bash
git add Dockerfile nginx.conf .dockerignore build CLAUDE.md
git add libs/hello-libs/hello-quick-links/src/jsMain/kotlin/com/bkahlert/hello/quicklink/QuickLinks.kt
git update-index --chmod=+x build
git commit -m "$(cat <<'EOF'
feat: ship hello-archive Docker image and one-command build script

Adds Dockerfile (nginx:alpine), nginx.conf, .dockerignore, and an
executable `build` script at the repo root that produces both Kotlin/JS
distributions and tags `hello-archive:latest`. The image serves web-app
at / and playground-app at /playground/, with a 301 from /playground.

Also fixes the Playground QuickLink to deep-link into the chatbot
showcase via /playground/#widgets/chatbot-widget — the bare hash would
not work because the web-app's WidgetRouter uses a different hash
format.

CLAUDE.md gets a short "Building & running" subsection describing the
new flow.

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>
EOF
)"
git log --oneline -1
```

Expected: `git log -1` shows the new commit. The script's executable bit is preserved because `git update-index --chmod=+x` runs before the commit captures the index state.

---

## Task 5 — Hand-off (no code changes)

**Goal:** Surface the finished branch state to the user and stop. Per spec hard constraints: do not push, do not open a PR without explicit go-ahead.

- [ ] **Step 1: Print the resulting commit log**

```bash
git log --oneline main..feat/archive
git status
```

Expected: four new commits (one per task above) on top of whatever was already on `feat/archive` when the session started. Working tree clean.

- [ ] **Step 2: Summarize and stop**

Report to the user:
- Four commits landed on `feat/archive`.
- The working tree is clean.
- No push, no PR — awaiting explicit go-ahead.
- Suggest next session: ClickUp resurrection (separate brainstorm/spec/plan, see the **Archival cut surface** section in `CLAUDE.md` for the verified cut points).

---

## Self-review against spec

**1. Spec coverage:**
- AppStore seam swap (sessionResolver + propsProvider) → Task 1, Steps 1-2.
- Delete `RemotePropsDataSource.kt` → Task 1, Step 2.
- Drop `com.bkahlert.aws.app` from both apps → Task 2, Steps 1-2.
- Reduce `environment.json` to `{}` → Task 2, Steps 3-4.
- Delete project-root `environment.json` files (added beyond the spec — verified necessary because the `aws-app` plugin wrote there, no auto-copy) → Task 2, Step 5.
- Delete `aws-cdk/`, `aws-lambdas/`, `build-logic/aws/`, `platforms/aws-platform/` → Task 3, Step 2.
- Remove `includeBuild` lines → Task 3, Step 1.
- Trim AWS sections from `CLAUDE.md` → Task 3, Step 3.
- Add Dockerfile, nginx.conf, .dockerignore, build → Task 4, Steps 2-5.
- QuickLink deep-link to chatbot showcase → Task 4, Step 1.
- Add "Building & running" CLAUDE.md section → Task 4, Step 6.
- Sign-in/sign-out round-trip verification → Task 1, Step 4.
- Curl-verified Docker routes → Task 4, Step 8.
- No push / no PR without go-ahead → Task 5.
- Archival cut surface stays in CLAUDE.md → Task 3, Step 3 explicit note.
- `gradle/libs.versions.toml` AWS-only entries removal — verified zero entries exist; no edit needed (noted in Task 2 preamble).

No spec requirement left unmapped.

**2. Placeholder scan:** None. Every step has either complete code, an exact command with expected output, or an explicit user-driven verification with the prompt to press Enter.

**3. Type / API consistency:** `FakeSession.Authorized()` (zero-arg) is used in Task 1, Step 1 — matches the `companion object` declaration in `FakeSession.kt:71-77` (default `userInfo = IdTokenPayload.JohnDoeInfo`). `StoragePropsDataSource(localStorage)` matches the existing fallback branch in the unmodified `AppStore.kt:44`. Plugin id `com.bkahlert.aws.app` is consistent across Task 2 (removal) and Task 3 (note about `build-logic/aws/` deletion). Hash route `widgets/chatbot-widget` is consistent across the QuickLink edit (Task 4, Step 1) and the verification (Task 4, Step 9).
