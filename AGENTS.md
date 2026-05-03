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

## yarn.lock churn

[build-logic/kotlin-project/src/main/kotlin/com/bkahlert/JsTargetDefaultOptions.kt](build-logic/kotlin-project/src/main/kotlin/com/bkahlert/JsTargetDefaultOptions.kt)
sets `yarnLockAutoReplace = true` and `yarnLockMismatchReport = NONE`. Every Kotlin/JS
task silently rewrites `libs/*/kotlin-js-store/yarn.lock` with whatever yarn re-resolves
to. Two symptoms follow from the same policy:

- **Local drift.** `git status` shows modified `yarn.lock` files the user didn't touch
  (typically additive entries for new caret-range resolutions of transitive deps). They
  regenerate on the next build — `git checkout -- libs/*/kotlin-js-store/yarn.lock` is
  the right answer; the diff is build artifact, not work.
- **CI yarn-cache races.** Parallel gradle invocations all rewrite the same lockfile.
  The CI commits `serialize gradle to dodge yarn cache race` and `split gradle into
  per-app invocations` are workarounds, not fixes.

The durable remedy is to flip both flags (`auto-replace = false`,
`mismatchReport = FAIL`), making the committed lockfile authoritative. Trade-off:
upstream transitive-dep shifts then require an intentional `chore: refresh yarn.lock`
PR rather than sneaking in. Treat this as a policy change — confirm with the user
before flipping; do not change it unilaterally just because the symptom appeared.
