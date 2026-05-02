# Museum branch + CI build — design

Branch: `feat/archive` (to be renamed `museum`). Goal: rename the archived
branch to `museum`, push it to `origin`, and add a GitHub Actions workflow
on that branch that builds the static apps with Gradle, builds a multi-arch
Docker image, smoke-tests it, and pushes it to Docker Hub as
`bkahlert/hello:museum` plus a per-commit pinned tag.

This design follows the previous archival work captured in
[2026-05-02-archive-hello-design.md](2026-05-02-archive-hello-design.md) and
the implementation plan
[2026-05-02-archive-hello.md](../plans/2026-05-02-archive-hello.md). The
reference for the workflow shape is
[fyta-dashboard's build.yml](../../../../fyta-dashboard/.github/workflows/build.yml).

## Goal & scope

### In scope

- Rename `feat/archive` → `museum` locally; push `museum` to `origin`.
  Leave `main` as the GitHub default branch. Don't delete anything.
- Add `HEALTHCHECK` to the existing
  [Dockerfile](../../../Dockerfile).
- Update the local [build](../../../build) script to tag the image
  `hello:museum` instead of `hello-archive:latest`, matching CI naming.
- Add an executable [smoke](../../../smoke) script at the repo root that
  runs the image, waits on `HEALTHCHECK`, and curls five known routes.
- Add `.github/workflows/build.yml` that orchestrates Gradle dist tasks,
  the smoke check, and a multi-arch push to `bkahlert/hello:museum` +
  `bkahlert/hello:museum-<short-sha>`.

### Out of scope

- Deploying the published image to `hello.museum.choam.de` /
  `hello.museum.bkahlert.com`. That is operator-facing infrastructure
  work and lives in a different session.
- Changing the GitHub default branch. `main` stays as the default and is
  preserved as the pre-archival snapshot for any future continuation of
  the AWS-stack work.
- Deleting `main` or any other branch. Nothing is destroyed.
- Fixing the pre-existing `:web-app:jsBrowserTest` failure (a webpack
  config-copy issue, unrelated to archival). CI runs only the dist tasks,
  which mirrors the local [build](../../../build) script and avoids the
  broken test.
- Publishing a `:latest` tag. With other variants (e.g. an eventual
  `:cloud` for the AWS-stack version) potentially coexisting,
  no variant is canonical, so `:latest` would create ambiguity.

### Success criteria

- `git branch --show-current` returns `museum`; `git ls-remote origin
  refs/heads/museum` returns the same SHA as the local tip.
- `git ls-remote origin refs/heads/main` is unchanged from before the
  rename.
- A push to `museum` triggers the workflow; the workflow succeeds end-to-end.
- After the run, `docker buildx imagetools inspect bkahlert/hello:museum`
  lists two manifests (`linux/amd64`, `linux/arm64`) and shows
  `provenance` + `sbom` attestations.
- `docker pull bkahlert/hello:museum-<short-sha>` works on both arm64
  (Mac) and amd64 (Linux).
- `docker run --rm -p 8080:8080 bkahlert/hello:museum`:
  - container `Health.Status` reaches `healthy` within ~10 s,
  - `GET /` → 200, `GET /playground` → 301, `GET /playground/` → 200,
  - `GET /web-app.js` → 200, `GET /playground/playground-app.js` → 200.
- Local `./build && ./smoke` succeeds end-to-end against the locally
  built `hello:museum` image.

## Branch rename

```bash
git branch -m feat/archive museum
git push -u origin museum
```

`feat/archive` was never pushed, so there is no stale remote ref to
clean up. The default-branch on GitHub stays as `main`. No deletions.

If anything else on the repo (CI badges, docs, hard-coded URLs) refers
to `feat/archive`, the rename surfaces those at search time and they
get fixed in the same commit. A pre-flight `git grep -n feat/archive`
covers it.

## Image identity

Docker Hub repo: `bkahlert/hello` (single repo, variants-by-tag —
modeled on `nginx:alpine`, `node:20-slim`).

Tags published per CI run:

| Tag | Mutability | Purpose |
|---|---|---|
| `bkahlert/hello:museum` | moving | "latest museum build" alias |
| `bkahlert/hello:museum-<short-sha>` | immutable | per-commit pin |

No `:latest`. No `:museum-latest`.

Local builds via [build](../../../build) tag `hello:museum` (no registry
prefix, so the local tag is unambiguously not-pushed).

Docker Hub credentials (`DOCKER_USERNAME`, `DOCKER_TOKEN`) already exist
on the GitHub repo, in line with the
[fyta-dashboard precedent](../../../../fyta-dashboard/.github/workflows/build.yml).
The `bkahlert/hello` repo on Docker Hub is auto-created on first push if
absent.

## Dockerfile change — HEALTHCHECK

Append to the existing [Dockerfile](../../../Dockerfile):

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/ || exit 1
```

`wget` is busybox-builtin in `nginx:alpine`, so no install layer.
`--spider` is HEAD-only (no body download). `--start-period=5s` covers
nginx startup so initial failures don't count toward the retry budget.

## `./build` script — tag rename + podman compat

In [build](../../../build), two changes:

1. Tag `hello:museum` instead of `hello-archive:latest`; update the
   trailing `Run:` / `Open:` help text. Local and CI image names are now
   identical (`hello:museum`); only the registry prefix differs
   (`bkahlert/` for the pushed copies).

2. Detect podman and pass `--format=docker` so `HEALTHCHECK` survives.
   Podman builds OCI-format images by default; the OCI image spec
   doesn't carry `HEALTHCHECK`, so podman strips it with a warning. The
   `--format=docker` flag switches to the Docker v2 schema which does.
   Real Docker's `docker build` doesn't accept `--format`, so the flag
   must only be passed when running under podman:

   ```bash
   build_args=()
   if [[ $(docker version 2>/dev/null) == *"Podman Engine"* ]]; then
       build_args+=(--format=docker)
   fi
   docker build "${build_args[@]}" -t hello:museum .
   ```

   The bash-glob match is intentional. `docker version | grep -qF
   'Podman Engine'` would be a natural fit but interacts badly with
   `set -o pipefail`: `grep -q` exits at first match, sends `SIGPIPE`
   to `docker version`, pipefail propagates the 141 exit — the `if`
   condition then evaluates false even though the substring is present.

CI runs real Docker via `docker/build-push-action`, so the workflow
itself is unaffected.

## `./smoke` script

New executable at repo root, sibling to [build](../../../build). Shape:

```
./smoke              # default: smoke-test the local hello:museum image
./smoke <image>      # smoke-test any image, e.g. bkahlert/hello:museum
```

Behavior:

1. `docker run -d --name <unique> -p 8080:8080 <image>`.
2. Poll `curl -fsI http://localhost:8080/` until 200 or a 30 s timeout
   (in which case dump container logs and fail). The image's `HEALTHCHECK`
   is intentionally **not** used as the readiness gate — podman doesn't
   auto-run healthchecks without systemd integration, so a status-based
   gate would never resolve there. URL polling tests the same property
   (HTTP serving) and is uniform across runtimes.
3. `curl -sI` each of the five URLs from the success-criteria list,
   assert the expected first line of the response.
4. Cleanup: `docker stop` (which `docker run --rm` would not do
   reliably on script abort) plus a `trap` to ensure cleanup on any exit
   path.
5. Exit 0 on all-pass, 1 on any failure (with the failing URL printed).

The implementer applies project bash conventions (rules/bash.md):
`#!/usr/bin/env bash`, `set -euo pipefail`, `tput`-based colors,
project icons (`✔` / `✘`), explicit error helper. `set -e` plus
the `trap` keep cleanup correct on all exit paths.

## Workflow file — `.github/workflows/build.yml`

```yaml
name: build

on:
  push:
    branches: [museum]
  workflow_dispatch:

permissions:
  contents: read

jobs:
  build-and-push:
    name: Build & push multi-arch museum image
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
      attestations: write
      id-token: write
    steps:
      - uses: actions/checkout@v5

      - name: Set up JDK 11 (Liberica)
        uses: actions/setup-java@v4
        with:
          distribution: liberica
          java-version: '11'

      - uses: gradle/actions/setup-gradle@v3

      - name: Build Kotlin/JS distributions
        run: ./gradlew :web-app:jsBrowserDistribution :playground-app:jsBrowserDistribution

      - uses: docker/setup-qemu-action@v3
      - uses: docker/setup-buildx-action@v3

      - name: Log in to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_TOKEN }}

      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: bkahlert/hello
          tags: |
            type=raw,value=museum
            type=raw,value=museum-{{sha}}

      - name: Build (amd64 only) for smoke test
        uses: docker/build-push-action@v6
        with:
          context: .
          file: Dockerfile
          platforms: linux/amd64
          load: true
          tags: hello:museum-smoke
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Smoke test
        run: ./smoke hello:museum-smoke

      - name: Build & push multi-arch
        id: push
        uses: docker/build-push-action@v6
        with:
          context: .
          file: Dockerfile
          platforms: linux/amd64,linux/arm64
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
          provenance: mode=max
          sbom: true
```

### Why two `build-push-action` steps

A multi-arch buildx run cannot `load: true` into the runner's local
docker daemon — buildx complains because the runner daemon doesn't
support multi-platform manifests. Workarounds (per-arch loaders,
manifest splitting) exist but are awkward.

Splitting the build into:

1. amd64 only, `load: true`, throwaway `hello:museum-smoke` tag → smoke
   test against the loaded image,
2. multi-arch, `push: true`, real tags from `metadata-action` → push.

…costs ~30 s for the second build because the buildx layer cache
(`type=gha,mode=max`) hits on the amd64 layer the first step just
produced. The arm64 layer builds fresh; the gradle output that both
arches `COPY` from is the same context bytes, so the `COPY` layer is
identical across both arches. Net cost vs. a single-step build is small,
and we never push an image that didn't pass smoke.

### Why no `tags: ['v*']` trigger

The `museum` branch is meant to sit. The fyta-dashboard model (push to
default branch + tag → release) doesn't apply here — there are no
release artifacts to cut from a frozen showcase. `workflow_dispatch`
covers the manual-rebuild case; pushes to `museum` cover the
"cherry-picked fix" case.

### Caching

- `gradle/actions/setup-gradle@v3` covers `~/.gradle/caches`,
  configuration cache, and the Kotlin Gradle plugin's bundled
  `~/.gradle/nodejs/` and `~/.gradle/yarn/` directories. Drop-in.
- `cache-from: type=gha` / `cache-to: type=gha,mode=max` covers docker
  layer cache for both buildx steps.
- No custom `actions/cache` is needed. The per-module
  `kotlin-js-store/yarn.lock` files are checked in, so yarn install is
  deterministic.
- GHA cache has a 10 GB / LRU limit. For an archived branch built
  occasionally, eviction between runs is likely; first-build-after-idle
  takes the full ~5 min. Acceptable.

## Verification (post-merge)

After the first push to `museum`:

1. Workflow turns green end-to-end.
2. `docker buildx imagetools inspect bkahlert/hello:museum` shows two
   manifests and `provenance` + `sbom` attestations.
3. From a clean machine: `docker run --rm -p 8080:8080
   bkahlert/hello:museum`, then run `./smoke bkahlert/hello:museum`.
   All five URL checks pass.
4. Local sanity: `./build && ./smoke` succeeds against `hello:museum`.

## Hard constraints

- Never commit on `main` / `master`. Work happens on `museum`
  (post-rename).
- Don't push the branch or trigger the workflow without explicit
  go-ahead from the user. The very first push *is* the workflow's first
  CI run, so this matters more than usual.
- JDK 11 (Liberica 11.0.19) for local builds; CI uses
  `actions/setup-java@v4 distribution: liberica java-version: '11'`.
- Composite-build task paths are `:web-app:jsBrowserDistribution` and
  `:playground-app:jsBrowserDistribution` (with leading colons).
