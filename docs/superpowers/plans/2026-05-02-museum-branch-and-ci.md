# Museum Branch + CI Build Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rename `feat/archive` → `museum`, push to `origin`, and add a GitHub Actions workflow that builds and publishes `bkahlert/hello:museum` (multi-arch) on every push to that branch.

**Architecture:** Three commits on `feat/archive`, then a local rename, then a push. Commit 1 lands image-flow improvements together (HEALTHCHECK in `Dockerfile`, `./build` retag, new `./smoke` script). Commit 2 lands the GitHub Actions workflow. After local verification (`./build && ./smoke` green), the branch is renamed to `museum` and pushed. The first push *is* the workflow's first CI run, so we gate on explicit user approval before the push.

**Tech Stack:** GitHub Actions, `docker/build-push-action@v6`, `docker/metadata-action@v5`, `actions/setup-java@v4` (liberica 11), `gradle/actions/setup-gradle@v3`, `nginx:alpine` (with busybox-wget HEALTHCHECK), bash for the local scripts.

**Spec:** [`docs/superpowers/specs/2026-05-02-museum-branch-and-ci-design.md`](../specs/2026-05-02-museum-branch-and-ci-design.md)

---

## Pre-flight

**Files:** none touched.

- [ ] **Step 1: Confirm branch and clean tree**

```bash
git branch --show-current
git status --short
```

Expected: branch is `feat/archive`. Status may show two unrelated yarn.lock files (`libs/hello-libs/kotlin-js-store/yarn.lock`, `libs/kommons-libs/kotlin-js-store/yarn.lock`) — these are pre-existing and must NOT be staged or modified. Anything else uncommitted: stop and ask.

- [ ] **Step 2: Confirm JAVA_HOME**

```bash
ls -d /Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19
export JAVA_HOME=/Users/bkahlert/Library/Java/JavaVirtualMachines/liberica-11.0.19
```

Expected: directory exists. If not, stop — Gradle 8.2.1 fails with cryptic errors on newer JDKs.

- [ ] **Step 3: Confirm docker daemon is running**

```bash
docker version --format '{{.Server.Version}}'
```

Expected: a version string. If the command errors, start Docker Desktop and re-run.

---

## Task 1 — Commit 1: HEALTHCHECK + `./build` retag + `./smoke` script

**Goal:** Land the three image-flow changes that hang together: nginx HEALTHCHECK in `Dockerfile`, retag `./build` output to `hello:museum`, add the new `./smoke` verification script.

**Files:**
- Modify: [`Dockerfile`](../../../Dockerfile)
- Modify: [`build`](../../../build)
- Create: [`smoke`](../../../smoke) (executable, no extension)

**Why one commit:** all three changes describe the same conceptual unit ("the local image-build flow now uses the `hello:museum` name and exposes a healthcheck and a smoke verifier"). Shipping them apart would leave intermediate states where `./build` and `./smoke` disagree on image name. Verification (Step 6) requires all three to be in place simultaneously.

- [ ] **Step 1: Add HEALTHCHECK to `Dockerfile`**

Replace the file contents with:

```dockerfile
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY apps/web-app/build/distributions/         /usr/share/nginx/html/
COPY apps/playground-app/build/distributions/  /usr/share/nginx/html/playground/
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/ || exit 1
```

`wget` is busybox-builtin in `nginx:alpine` (no install layer). `--spider` is HEAD-only. `--start-period=5s` covers nginx startup before retry budget counts.

- [ ] **Step 2: Retag the image in `./build` and add podman-compat detect**

Edit [`build`](../../../build):

```diff
-# build — produce both Kotlin/JS distributions and the hello-archive
-#         Docker image.
+# build — produce both Kotlin/JS distributions and the hello:museum
+#         Docker image.
```

Replace the single `docker build` line with a podman-detecting block:

```diff
-docker build -t hello-archive:latest .
+build_args=()
+if [[ $(docker version 2>/dev/null) == *"Podman Engine"* ]]; then
+    build_args+=(--format=docker)
+fi
+docker build "${build_args[@]}" -t hello:museum .
```

Why: podman builds OCI-format images by default; OCI doesn't carry
`HEALTHCHECK`, so podman would silently strip it (with a warning)
unless we pass `--format=docker`. Real Docker doesn't accept `--format`,
so the flag must be conditional.

The bash-glob match is deliberate. `docker version | grep -qF 'Podman
Engine'` would be cleaner-looking but breaks under `set -o pipefail`:
`grep -q` exits early on match, sends `SIGPIPE` to `docker version`,
pipefail propagates the 141 — the `if` condition then reads false even
though the substring is present.

Then update the help text:

```diff
-printf '\n%s✔%s Built hello-archive:latest\n\n' "$green" "$reset"
-printf 'Run:   docker run --rm -p 8080:8080 hello-archive:latest\n'
+printf '\n%s✔%s Built hello:museum\n\n' "$green" "$reset"
+printf 'Run:   docker run --rm -p 8080:8080 hello:museum\n'
```

The `Open:` lines below are unchanged.

- [ ] **Step 3: Create `./smoke`**

Create the file at the repo root with these contents:

```bash
#!/usr/bin/env bash
#
# smoke — run a hello:museum image and verify the five known routes.
#
# Usage:
#   ./smoke               # verify the local hello:museum image
#   ./smoke <image>       # verify any image, e.g. bkahlert/hello:museum
#
# Prerequisites:
#   - docker daemon running
#   - host port 8080 free

set -euo pipefail

green=$(tput setaf 2 2>/dev/null || true)
red=$(tput setaf 1 2>/dev/null || true)
yellow=$(tput setaf 3 2>/dev/null || true)
reset=$(tput sgr0 2>/dev/null || true)

image=${1:-hello:museum}
container=museum-smoke-$$

cleanup() {
    docker stop "$container" >/dev/null 2>&1 || true
    docker rm   "$container" >/dev/null 2>&1 || true
}
trap cleanup EXIT

printf '%s⚙%s Starting %s as %s\n' "$yellow" "$reset" "$image" "$container"
docker run -d --name "$container" -p 8080:8080 "$image" >/dev/null

printf '%s⚙%s Waiting for HTTP / to respond\n' "$yellow" "$reset"
ready=false
for _ in $(seq 1 30); do
    if curl -fsI http://localhost:8080/ >/dev/null 2>&1; then
        ready=true
        break
    fi
    sleep 1
done

if [[ $ready != true ]]; then
    printf '%s✘%s timed out waiting for HTTP / to respond\n' "$red" "$reset" >&2
    docker logs "$container" >&2
    exit 1
fi

fail=0
check() {
    local url=$1 want=$2 got
    got=$(curl -sI "$url" | head -1 | tr -d '\r')
    if [[ $got == *"$want"* ]]; then
        printf '%s✔%s %-58s %s\n' "$green" "$reset" "$url" "$got"
    else
        printf '%s✘%s %-58s %s (expected %s)\n' "$red" "$reset" "$url" "$got" "$want"
        fail=1
    fi
}

check http://localhost:8080/                              "200"
check http://localhost:8080/playground                    "301"
check http://localhost:8080/playground/                   "200"
check http://localhost:8080/web-app.js                    "200"
check http://localhost:8080/playground/playground-app.js  "200"

if [[ $fail -eq 0 ]]; then
    printf '\n%s✔%s smoke passed\n' "$green" "$reset"
else
    printf '\n%s✘%s smoke failed\n' "$red" "$reset" >&2
fi

exit $fail
```

Style notes (per `~/.config/agents/rules/bash.md`):
- Shebang `#!/usr/bin/env bash`. Yes.
- No file extension. Yes.
- Header with purpose and usage. Yes.
- No hardcoded escape sequences — `tput`. Yes.
- Icons from project palette (`⚙` task, `✔` success, `✘` error). Yes.
- Single positional arg (`<image>`) is acceptable per "prefer named parameters over positional arguments (unless those are actual files)". `<image>` is image-tag-shaped, not a flag. Treating it as positional matches `docker run`'s own ergonomics.

- [ ] **Step 4: Make `./smoke` executable**

```bash
chmod +x smoke
```

- [ ] **Step 5: Verify shellcheck-clean (best effort)**

```bash
command -v shellcheck && shellcheck smoke build
```

Expected: clean, or `shellcheck` not installed (acceptable — skip). If shellcheck reports issues, fix them and re-run.

- [ ] **Step 6: End-to-end local verification — `./build && ./smoke`**

```bash
./build
./smoke
```

Expected:
- `./build` finishes with `✔ Built hello:museum`.
- `./smoke` walks through: starting container → waiting healthy → five `✔` lines for the URL checks → `smoke passed`.

If any of the URL checks fail, that's the load-bearing signal. Most likely culprits: `nginx.conf` typo (no — wasn't touched), missing dist files (rerun `./build`), HEALTHCHECK syntax issue (re-read the Dockerfile line carefully).

- [ ] **Step 7: Commit**

```bash
git add Dockerfile build smoke
git update-index --chmod=+x smoke
git commit -m "$(cat <<'EOF'
feat: add HEALTHCHECK, retag image hello:museum, add smoke script

Three coordinated changes to the local image flow:

- Dockerfile gains a HEALTHCHECK using busybox-wget (already in
  nginx:alpine) so docker reports container health.
- ./build now tags hello:museum to match the CI naming
  (bkahlert/hello:museum); the local tag carries no registry prefix so
  it's unambiguously a local-only build.
- New ./smoke script runs an image, polls HTTP readiness, and curls the
  five known routes (/, /playground, /playground/, /web-app.js,
  /playground/playground-app.js). Used both locally and from CI.

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>
EOF
)"
git log --oneline -1
```

Expected: `git log -1` shows the new commit.

---

## Task 2 — Commit 2: GitHub Actions workflow

**Goal:** Add the workflow that fires on push to `museum` (and manual dispatch), runs gradle dist tasks, smoke-tests an amd64-only build, then pushes a multi-arch image to Docker Hub with provenance + SBOM.

**Files:**
- Create: `.github/workflows/build.yml`

**Note:** the workflow references the `museum` branch in its `on.push.branches`, but the branch doesn't exist yet — that's deliberate. The workflow file is committed first; the branch rename happens in Task 3; the push in Task 4 is what actually triggers a run. Until then the file is dormant, and the YAML is valid syntactically — GitHub will not error on a missing branch reference.

- [ ] **Step 1: Create `.github/workflows/build.yml`**

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

- [ ] **Step 2: YAML lint (best effort)**

```bash
command -v yamllint && yamllint -d 'rules: {line-length: disable}' .github/workflows/build.yml
```

Expected: clean, or `yamllint` not installed (acceptable — skip).

- [ ] **Step 3: Verify the file parses as YAML**

```bash
python3 -c 'import yaml,sys; yaml.safe_load(open(".github/workflows/build.yml")); print("OK")'
```

Expected: `OK`. If it errors, fix indentation and re-run.

- [ ] **Step 4: Commit**

```bash
git add .github/workflows/build.yml
git commit -m "$(cat <<'EOF'
ci: add multi-arch museum image build workflow

Fires on push to museum + workflow_dispatch. Builds both Kotlin/JS
distributions with Gradle (just the dist tasks — :web-app:jsBrowserTest
is broken upstream and unrelated to archival), then builds an
amd64-only image, smoke-tests it via ./smoke against the five known
routes, and on success pushes a multi-arch image
(linux/amd64,linux/arm64) to Docker Hub as bkahlert/hello:museum and
bkahlert/hello:museum-<short-sha>, with provenance and SBOM
attestations.

DOCKER_USERNAME / DOCKER_TOKEN secrets already exist on the GitHub repo
(same setup as bkahlert/fyta-dashboard).

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>
EOF
)"
git log --oneline -1
```

Expected: `git log -1` shows the new commit.

---

## Task 3 — Local branch rename

**Goal:** Rename `feat/archive` to `museum` in the local clone.

**Files:** none touched. Pure git operation.

**Note:** the branch carries the two new commits with it — they remain on the renamed branch.

- [ ] **Step 1: Confirm we're on the right branch and tree is clean**

```bash
git branch --show-current
git status --short
```

Expected: `feat/archive`, status only shows the two pre-existing yarn.lock files (or empty).

- [ ] **Step 2: Sweep for stray `feat/archive` references in the repo**

```bash
git grep -n 'feat/archive' || echo 'no matches'
```

Expected: `no matches`. If any tracked file references the old branch name (CI badge URL, doc, hardcoded path), report each and stop — those need to be updated as part of the rename, not after.

- [ ] **Step 3: Rename the branch**

```bash
git branch -m feat/archive museum
git branch --show-current
```

Expected: `museum`.

- [ ] **Step 4: Verify upstream tracking is gone**

```bash
git rev-parse --abbrev-ref --symbolic-full-name '@{u}' 2>&1 || true
```

Expected: error message ("no upstream configured" or similar). The local rename does not auto-push or auto-set upstream — that happens explicitly in Task 4.

---

## Task 4 — Push to origin (USER GATE)

**Goal:** Push the newly-renamed `museum` branch to `origin`. **This is the moment the workflow first fires.**

**Files:** none touched. Pure git operation.

**Per spec:** "Don't push the branch or trigger the workflow without explicit go-ahead from the user."

- [ ] **Step 1: Show the user what would be pushed**

```bash
git log --oneline origin/main..museum | head -20
echo '---'
git rev-parse museum
git rev-parse origin/main
```

Display these commits. The push will create a new remote branch `origin/museum` with these commits on top of the existing `main` ancestry.

- [ ] **Step 2: Wait for explicit user approval**

Halt and prompt:

> "Ready to `git push -u origin museum`. This will create the `origin/museum` branch and trigger the first CI run, which on success will publish `bkahlert/hello:museum` to Docker Hub. Approve?"

Wait for explicit approval. If declined, stop and report.

- [ ] **Step 3: Push**

```bash
git push -u origin museum
```

Expected: push succeeds and reports `Branch 'museum' set up to track 'origin/museum'`.

- [ ] **Step 4: Confirm the remote ref**

```bash
git ls-remote origin refs/heads/museum
git ls-remote origin refs/heads/main
```

Expected: the first command's SHA matches `git rev-parse museum`. The second command's SHA is unchanged from before — proves we did not affect `main`.

---

## Task 5 — Verify first CI run

**Goal:** Watch the workflow's first run end-to-end and confirm the published image works.

**Files:** none touched. Read-only inspection.

- [ ] **Step 1: Watch the workflow run**

```bash
gh run watch --repo bkahlert/hello
```

If `gh run watch` immediately exits because the run is already complete, fetch the most recent run's status:

```bash
gh run list --repo bkahlert/hello --workflow build.yml --limit 1
```

Expected: status `completed`, conclusion `success`. If failure, dump the failing job's logs:

```bash
gh run view --repo bkahlert/hello --log-failed
```

…and stop. Report the failure.

- [ ] **Step 2: Inspect the published manifest list**

```bash
docker buildx imagetools inspect bkahlert/hello:museum
```

Expected output mentions both `linux/amd64` and `linux/arm64` manifests, and lists `provenance` + `sbom` attestations.

- [ ] **Step 3: Pull and smoke-test the published image**

```bash
docker pull bkahlert/hello:museum
./smoke bkahlert/hello:museum
```

Expected: `./smoke` reports five `✔` URL lines and `smoke passed`.

- [ ] **Step 4: Sanity-check the per-commit-pinned tag**

```bash
short_sha=$(git rev-parse --short museum)
docker pull bkahlert/hello:museum-$short_sha
docker image inspect bkahlert/hello:museum   bkahlert/hello:museum-$short_sha \
  --format '{{.RepoDigests}}'
```

Expected: both tags resolve to the same digest (they were pushed in the same `build-push-action` step).

- [ ] **Step 5: Report and stop**

Summarize to the user:
- branch `feat/archive` renamed to `museum`, pushed to `origin/museum`
- two new commits on `museum` (HEALTHCHECK + retag + smoke; CI workflow)
- workflow run #1 succeeded
- `bkahlert/hello:museum` and `bkahlert/hello:museum-<sha>` published, multi-arch, with attestations
- five smoke checks pass against the published image

No PRs, no further pushes. The deferred ClickUp resurrection (see the **Archival cut surface** section in [`CLAUDE.md`](../../../CLAUDE.md)) remains as the next session's work.

---

## Self-review against spec

**1. Spec coverage:**
- Branch rename `feat/archive` → `museum` → Task 3.
- Push `museum` to `origin`; leave `main` as default → Task 4 (gated).
- Sweep for stray references to old branch name → Task 3, Step 2.
- HEALTHCHECK in Dockerfile → Task 1, Step 1.
- Update `./build` to tag `hello:museum` → Task 1, Step 2.
- New `./smoke` script → Task 1, Step 3-4.
- `.github/workflows/build.yml` → Task 2, Step 1.
- Image `bkahlert/hello:museum` + `bkahlert/hello:museum-<short-sha>`, no `:latest` → Task 2, metadata-action `tags:` block; verified in Task 5, Step 4.
- Two-step build (amd64 smoke → multi-arch push) → Task 2, Step 1 (build-push-action × 2).
- Smoke verifies image readiness → `./smoke` polls `curl http://localhost:8080/` until 200 (Task 1, Step 3). HEALTHCHECK still ships in the image for orchestrators, but smoke does not gate on it (podman doesn't auto-run healthchecks without systemd; URL polling is uniform across runtimes).
- Caching: `setup-gradle` + `type=gha` buildx → Task 2, Step 1.
- `DOCKER_USERNAME` / `DOCKER_TOKEN` already exist → Task 2 commit message, Task 5 verification.
- Triggers: push to `museum` + workflow_dispatch, no `tags: ['v*']` → Task 2, Step 1.
- `./gradlew build` not used; only dist tasks → Task 2 commit message.
- Permissions block matches reference workflow → Task 2, Step 1.
- Provenance + SBOM → Task 2, Step 1.

No spec requirement left unmapped.

**2. Placeholder scan:** None. Every step has either complete code, an exact command with expected output, or an explicit user-driven verification with a halt-and-prompt.

**3. Type / API consistency:**
- Image tags `bkahlert/hello:museum` and `bkahlert/hello:museum-<sha>` consistent across Task 2 metadata-action, Task 5 verification, and spec.
- Local tag `hello:museum` used identically in Task 1 (`./build`), Task 1 (`./smoke` default), and Task 2 (`hello:museum-smoke` for CI's local-only smoke build).
- Smoke readiness contract: poll `curl http://localhost:8080/` until 200, 30 s timeout. Independent of HEALTHCHECK (podman doesn't auto-run it without systemd integration).
- Five-URL smoke list identical between `./smoke` (Task 1) and the spec's success-criteria list.
- Branch name `museum` consistent across `on.push.branches` (Task 2), `git branch -m` (Task 3), `git push -u origin museum` (Task 4), `gh run` queries (Task 5).
