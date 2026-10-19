# AGENTS.md

## Build, Test, And Validation

### Common Commands

- `./gradlew test`
- `./gradlew pluginJars` (the shaded plugin jars of every platform)
- `./gradlew test assembleDistribution` (what the CI checks run, with `integrationTest`)
- `./gradlew integrationTest` (tests tagged `integration`, which start database servers in containers; needs Docker)

`assembleDistribution` collects the shipped files under their final names in `build/distribution/`; the root `toolkitDistribution` block declares them, and workflows upload that directory as it is. `VERSION` sets the version, `dev` by default.

### Live Tests

`socialismus-testing` starts the packaged jars on real servers through Anvil and drives simulated players through them. `socialismus-testing/JOURNEYS.md` plans the journeys by suite; tick a journey off in the pull request that adds it, and add one there when a feature needs it.

- every journey class carries the tag of its suite (`chat`, `commands`, `platform`, `network`, `sync`, `upgrade`, `modules`, and one per module built for Socialismus: `chirper`, `dialogue`, `essentials`, `bubbler`, `database`), and each suite has an `anvil<Suite>Test` task; `anvilTest` runs only `JourneySuitesTest`, which fails when a journey is in no suite or in several
- run one suite with `./gradlew :socialismus-testing:anvilChatTest`, or one journey with `--tests 'me.whereareiam.socialismus.testing.chat.*'`; `./gradlew :socialismus-testing:anvil` runs them all; a journey takes about half a minute, several run at once (`-Psocialismus.journeys.parallelism=1` runs them one after another)
- declare the network with `@Socialismus(network = ..., bundle = ..., modules = ..., moduleFixtures = ..., containers = ..., data = ..., configure = ..., proxyConfigure = ...)`: `network` picks one server, a proxy, or a proxy with the backends `lobby` and `survival` (`BACKENDS`, `PROXY_AND_BACKENDS`, `PROXY_AND_LOBBY`); `modules` installs module jars in `modules/` and `moduleFixtures` the altered jars of the module-system journeys; `containers` runs containers such as Redis as processes of the network, on Docker while the servers run on the host, which files reach through Anvil's `${anvil.process.redis.host}` and `${anvil.process.redis.port}`; `configure` classes write Socialismus's files through its models (`ServerFiles`, or `ServerFiles.text` for a file with such placeholders), `proxyConfigure` adds files only the proxy gets, and `data` starts from a directory of the test resources, such as `data/2.0.0-RC6`
- drive players with `Player.joins(anvil, "Alice")`, `switchesTo(Network.SURVIVAL)`, `chat`, `command`, `sees` and `neverSees`, the console with `Console.of(anvil)` or `Console.of(anvil, Network.LOBBY)`, and any process, a container included, with `anvil.processes().freeze("redis")`, `thaw`, `kill` and `restart`; `sees` only looks at what arrived since the player's previous step
- the journeys' classpath leaves out Adventure, which Anvil's player workers only take from their protocol library, so files are written with plain Configura
- a failed journey keeps its network under `socialismus-testing/build/anvil/`; read the servers' `logs/latest.log` there

### Convention Plugins

A module's build file holds its role, dependencies and metadata; shared behaviour lives in `build-logic`:

| Plugin | For |
| --- | --- |
| `module-java` | every Java module: Java 21 toolchain, Lombok, the libraries Socialismus provides at runtime, JUnit |
| `module-api` | a published API: `module-java` plus Maven publishing |
| `packaging-shaded-jar` | a jar that bundles its dependencies, with the relocations of `packaging-relocations` |
| `packaging-platform-jar` | a plugin jar; `platform { name.set("PAPER") }` names its classifier and `Plugin-Type` and it fills `plugin.yml` and `paper-plugin.yml` |
| `packaging-bundle-jar` | the bundle jar: every platform whose `platform` block keeps `bundled`, with the descriptors it lists in `descriptors` |
| `packaging-module-jar` | the jar of a Socialismus module; `socialismusModule { name.set("Chirper") }` names the jar and fills `module.json` |

The modules compile with Java 21 whichever Java runs Gradle; `platform-bukkit-paper` uses a Java 25 toolchain, because the Paper API of 26.x is built for Java 25.

### Module Families

A family is a folder holding its assembly, which only wires the parts together, beside its `-api` and `-common`:

```text
socialismus-module/
  module/           assembly: ModuleConfiguration     :socialismus-module:module
  module-api/       contracts for modules              :socialismus-module:module-api
  module-common/    loading, resolving, providers      :socialismus-module:module-common

modules/module-dialogue/
  dialogue/         assembly, the Dialogue jar         :module-dialogue
  dialogue-api/     contracts, models, stores          :module-dialogue-api
  dialogue-common/  logic, config, in-memory stores    :module-dialogue-common
  dialogue-command/ /message, /reply                   :module-dialogue-command
  dialogue-network/ delivery over SocialismusNetwork   :module-dialogue-network
```

- logic belongs in `-common`, never in the assembly
- add a further side, such as `-command`, only when it exists; a small family starts with its assembly alone
- a module's runtime libraries are Attache libraries of its own: declare them with `attache(...)` in the part that uses them (applying the `me.whereareiam.attache` plugin there), and Socialismus loads them into the module's class loader before its main class; put optional ones, such as one database driver, in a `group(...)` and load it with `libraries.loadGroups(...)` from the module
- a module shares its API, and the libraries that API exposes, with the modules that depend on it through `socialismusModule { exports.add("org.jdbi") }`: package names, each with its subpackages; the build writes them to `module.json` and `checkModuleExports` fails when one has no class in the module or on its classpath
- a module's `module.json` says what it is built for and what it needs, one fact in one place:
  - `socialismus`: the Socialismus versions, filled by the build (`@socialismus@`)
  - `platforms`: where it runs, any one of them, such as `{"type": "PAPER", "version": "26\\.1(\\..*)?"}`; the detected platform is exact, so Folia runs a module only when it lists `FOLIA`
  - `dependencies`: `MODULE` (another module; its exports reach the dependent), `INTEGRATION` (a plugin through Socialismus's integration) and `PLATFORM` (a plugin the platform loaded; all its classes, looked up after the module's own); any of them may be `optional`
  - versions are regular expressions the whole version must match, and any version when left out
- modules stop in reverse dependency order, so a module still reaches what it depends on while it stops
- `socialismus-testing/fixture/` holds modules built only for the module journeys, such as one that exports a package and one that depends on it

### Platforms

Platforms are grouped under `socialismus-platform/type/` by what their members share; beside it, `common/` holds what every platform shares and `bundle/` builds one jar for several platforms:

```text
socialismus-platform/
  bundle/               PAPER and VELOCITY in one jar  :socialismus-platform:bundle
  common/               shared by every platform       :socialismus-platform:common
  type/
    platform-bukkit/    the Bukkit API
      common/           shared by Bukkit and Paper     :platform-bukkit-common
      bukkit/           BUKKIT jar                     :platform-bukkit-bukkit
      paper/            PAPER jar                      :platform-bukkit-paper
    platform-proxy/     proxies
      velocity/         VELOCITY jar                   :platform-proxy-velocity
```

- `common/` starts Socialismus (`PlatformInjector`), binds the integrations every platform offers (`PlatformConfiguration`), keeps scheduled tasks (`PlatformScheduler`) and reports the shared bStats charts (`PlatformMetrics`); a platform adds only what its own API needs
- the bundle merges the classes of its platforms, so it only carries platforms whose shared classes are identical; Bukkit relocates Adventure and sets `bundled.set(false)`
- `./gradlew runDevBundle` starts Velocity and Paper with the bundle jar
- a new platform joins the group it shares code with, such as BungeeCord in `platform-proxy/`; a group gets its `common/` once two members share something

## Branching And Pull Requests

### Branch Flow

- Build each feature, fix, or isolated piece of work in its own `feature/...` branch.
- Merge feature work into `dev` through a pull request. Pull requests are merged by rebase: each commit lands on `dev` as written, so a branch holds clean conventional commits and no fixups.
- Releases are cut from `dev`: publishing the release draft tags the current `dev`.

### Pull Request Checks

- Every pull request gets the metadata check (title and label) and the checks: `test` and `assembleDistribution`, and one job per journey suite that has journeys.
- The `dev` ruleset requires `Title and labels` and `Checks / Result`, which passes only when the build and every journey suite pass, so a new suite needs no change to the ruleset.
- Dependabot groups minor and patch updates into one pull request per ecosystem, and `dependabot-auto-merge.yml` turns on auto-merge for it, so it merges once the checks pass; a major update gets a pull request of its own and waits for review. The merge goes through a GitHub App, because a merge by `GITHUB_TOKEN` would not start the workflows of the push to `dev`; its `MERGE_APP_CLIENT_ID` and `MERGE_APP_PRIVATE_KEY` are Dependabot secrets, since a run Dependabot starts gets no Actions secrets.
- Label a pull request with exactly one of `feature`, `change`, `bug`, `dependencies`, or with `skip-changelog`; `major` may accompany a category.
- Add `module:chirper`, `module:bubbler`, `module:dialogue`, `module:essentials`, `module:redis`, `module:channelizer`, `module:database` or `module:network` when a pull request or issue concerns that module's jar; work on the plugin itself carries no such label.
- A run keeps as artifacts only what someone downloads: `socialismus-plugins-<version>` from a development build or release, and `test-results` when a test failed.
- Development builds are started manually and publish a branch-qualified version. A published GitHub release publishes Maven artifacts, attaches the jars, and publishes to the stores; a manual run of the release workflow only republishes to the stores.
- Entry workflows only select a trigger and call `reusable-verify.yml` and `reusable-publish.yml`; shared steps come from `whereareiam/devops` and `whereareiam/blockops`.
- The Gradle build cache is on. Pull requests only read caches, so `build-cache.yml` compiles on every push to `dev` and writes the Gradle cache; when the checks start running other tasks before their tests, add those tasks to its command.

### Issues

- A suggestion opens with `suggestion`, the label of an idea not decided on yet. An accepted one trades it for `feature` or `change`; a declined one is closed as not planned.
- A bug report opens with `bug`.

### Release Notes

A merged pull request lands in the release draft as `* <title> ([#<number>](<url>))`, under the category of its label. The draft is written for server admins and players, so the title and the label decide what they read.

#### Titles

- use the strict format `Area: Description`, with a past-tense description of what changed for the reader: `Introduced`, `Changed`, `Fixed`, `Removed`
- describe the effect, not the implementation: `Chat: Fixed messages of muted players reaching the global chat`, not `Chat: Checked the mute state in the chat pipeline`
- the draft sorts by the exact area, so spell an area exactly as listed below

Areas for admins and players:

`Chat`, `Commands`, `Configuration`, `Integrations`, `Messages`, `Modules`, `Placeholders`, `Platform`, `Synchronization`, `Updates`, `Chirper`, `Bubbler`, `Dialogue`, `Essentials`, `SocialismusRedis`, `SocialismusChannelizer`, `SocialismusDatabase`, `SocialismusNetwork`

- reuse an existing area whenever one fits; a new admin command is `Commands`, a new setting in a module's file belongs to that module's area
- add a new area only for something admins recognise by that name, such as a new feature, module, or config file, and add it to this list in the same pull request
- never use Gradle module, package, or class names as an area: no `Common`, `Adapter`, `Runtime`, `Bootstrap`, `Printer`

`API:` is for changes a module or plugin developer compiles against or listens to: public types, events, and module contracts. The drafter moves `API:` changes out of their category into a closing "For developers" section. A change that matters to both admins and developers takes the admin area; mention the API part in the pull request body.

#### Labels

| Label | Use for | Draft section |
| --- | --- | --- |
| `feature` | something new for admins, players, or developers | What’s new |
| `change` | changed behavior, configuration, or API | Enhancements |
| `bug` | a fix for a bug that shipped in a release | Fixes |
| `dependencies` | a library update | Dependencies |
| `skip-changelog` | anything a reader of the release would not notice | not listed |

Use `skip-changelog` for:

- internal refactors with no effect on admins, players, or the public API
- build, CI, test, and documentation-only changes
- a fix for a bug that never shipped: when the broken code was introduced after the last release, the fix belongs to the work that introduced it, not to the release notes; compare against the last release tag (`git grep <symbol> <tag>`, `git log <tag>..HEAD -- <path>`) to decide
- changes to something that was itself added after the last release and never shipped

Add `major` next to the category when admins or developers must act on upgrade: configuration moved or reset without a migration, stored data that is not carried over, or an incompatible public API change. One `major` pull request makes the draft a major version.

#### Release notes file

The drafter rebuilds the draft on every push to `dev`, so text typed into the draft is lost. Everything written by hand, such as the subtitle, the summary, upgrade notes, and security callouts, lives in `.github/release-notes.md` and is changed through pull requests like any other file.

```markdown
---
version: 2.0.0
---

# Update 2.0.0 - Modules

One or two sentences about the release.

## Upgrading

* Update from 2.0.0-RC6; older versions are not supported.
```

- the content replaces the subtitle and summary placeholders of the draft only while `version` matches the version the draft resolves to; notes of a published release therefore never reach the next draft
- when a change needs a note, add it to this file in the same pull request; if the file names an already published version, start the notes of the next release by replacing the whole file and setting `version` to the version the draft shows
- keep the heading as `# Update <version> - <subtitle>`; the stores take the subtitle as the version name
- bullets in this file are not sorted into the changelog

#### Publishing a release

- fix the changelog part of the draft by editing titles and labels of merged pull requests, then rerun the `Release drafter` workflow
- the subtitle and summary come from `.github/release-notes.md`; the release workflow stops while a placeholder is left
- the summary names what admins must do on upgrade (everything labelled `major`) and calls out security fixes
- the release workflow attaches `release-notes-bbcode.txt` for stores without automatic upload, such as SpigotMC and spigotmc.ru
