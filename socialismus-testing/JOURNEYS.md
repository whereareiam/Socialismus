# Journeys

Live player journeys start the packaged Socialismus jars on real servers through
[Anvil](https://github.com/whereareiam/anvil) and drive simulated players through them. This file
is the plan: every journey Socialismus should have, grouped into the suites they will run in. Tick a
journey off in the pull request that adds it, and add a line here when a feature needs a new one.

## How the suites work

- Each suite is a JUnit tag and an `anvil<Suite>Test` task, such as `anvilChatTest`; CI runs one job
  per suite, like Identica.
- A journey belongs to exactly one suite; a check fails when one is in none or in several.
- A journey gets a fresh network of its own, declared on the test class, and names player-facing
  messages through Socialismus's message models instead of copying their text.
- Every suite that has journeys runs on every pull request; `upgrade` moves to the release workflow once
  it outgrows that.

Journeys run on Minecraft 26.1.2, the newest version Anvil's players speak: Paper 26.1.2,
Spigot 26.1.2 and Velocity 3.5.1.

## What Anvil can observe

Players send chat and commands and receive chat as plain text, including action bars sent as chat,
and Anvil models the chat lines a player can still scroll back to. Anvil also records titles, action
bars, boss bars and sounds, the entities around a player with their names and display text, the
command tree a player holds, the server a player is on, kicks and console output, and lets players
move and use items.

## First wave

The pull request that added the `socialismus-testing` module brought the infrastructure and the
journeys marked **first**:

- [x] the module with Anvil 1.0.0, the Paper, Spigot and Velocity platforms, and the PAPER, BUKKIT,
  VELOCITY and BUNDLE jars as artifacts; the module jars follow with the `modules` suite
- [x] `@Socialismus`, which starts a Paper server, a Spigot server, or a Velocity proxy in front of a
  Paper backend, and writes Socialismus's files through its configuration models
- [x] the suite check, a CI job per suite that has journeys, and the build cache job preparing
  Anvil's servers
- [x] a "Live Tests" section in `AGENTS.md`

The first wave found three bugs: `/socialismus reload` sometimes kept the old chats, chat reached
nobody on Velocity, and the BUKKIT jar did not start on Spigot. Their journeys run now that the fixes
have landed.

## chat

One Paper server unless stated otherwise.

- [x] A message reaches every player in the format of the chat it matched **first**
- [x] A symbol trigger picks its chat and is stripped from the message, such as `!` for global chat **first**
- [x] Local chat reaches only the players within its radius **first**
- [x] A permission requirement decides who may send in a chat and who receives it
- [x] A world requirement keeps a chat to the players of its worlds
- [x] A sender a chat's requirements refuse is told why in chat's wording, or in `messages.yml`'s where chat
  words nothing, such as the permission or the worlds the chat needs
- [x] A requirement group's own reason is told before any other wording
- [x] A message requirement selects the chat by the sender's previous message
- [ ] A placeholder requirement selects the chat when PlaceholderAPI is installed
- [x] The chat with the highest priority wins when several match
- [x] A message takes the last format whose requirements its sender meets
- [x] A message whose sender meets none of its chat's formats gets the no-format message
- [ ] A chat shows each recipient the format whose requirements they meet: Socialismus picks one
  format for the sender and does not read a format's recipient requirements yet
- [x] A regex trigger sends to its chat
- [ ] A command trigger sends to its chat: Socialismus reads command triggers but does not act on them yet
- [x] A disabled chat is skipped
- [x] A message that matches no chat gets the no-chat message
- [x] Chats from several files in `chat/list/` are all loaded
- [x] Legacy serialization renders a format as MiniMessage does
- [x] Only the players who meet chat history's requirements see the clear button
- [ ] PlaceholderAPI placeholders are filled in formats when PlaceholderAPI is installed
- [x] `/socialismus toggle <chat>` keeps a chat from the player, except from senders who meet `toggle.bypass`; a chat with `toggleable: false` refuses

## commands

- [x] `/socialismus help` lists the commands the player may use **first**
- [x] `/socialismus reload` applies a changed format without a restart **first**
- [x] `/socialismus clear` clears the chat of the players
- [x] `/socialismus clear` keeps the messages of a player who meets chat history's bypass requirements
- [x] `/socialismus debug` reports the platform and version
- [x] A player without permission neither sees nor runs the administrator commands

## platform

- [x] The PAPER jar starts on the latest Paper and a player chats **first**
- [x] The BUKKIT jar starts on the latest Spigot and a player chats **first**
- [x] The VELOCITY jar starts on Velocity alone and a player chats **first**
- [x] The BUNDLE jar starts on Paper and on Velocity **first**
- [ ] The PAPER jar starts on Paper 1.20.6, the oldest it supports
- [ ] The BUKKIT jar on a recent Paper recommends the PAPER jar
- [ ] The PAPER jar refuses to start on Paper below 1.20.6 with a reason

## network

A Velocity proxy with Paper backends.

- [ ] A proxy chat reaches the players on every backend
- [ ] A server requirement keeps a chat to the players of its servers
- [ ] A split installation delivers the proxy's staff chat and the backends' local and global chats, none of them twice
- [ ] A player who switches servers stays in the proxy's chats
- [ ] PAPIProxyBridge fills backend placeholders in proxy formats

## sync

Two Paper backends, lobby and survival, behind a Velocity proxy: `Network.BACKENDS` runs Socialismus on both
backends, `PROXY_AND_BACKENDS` on the proxy as well, and `PROXY_AND_LOBBY` on the proxy and the lobby. Redis runs
as a container process of the network (`containers = Container.REDIS`), so those journeys need Docker.

- [x] SocialismusRedis brings its Redis client and refuses to load with a reason when Redis cannot be reached
- [x] SocialismusNetwork joins the network under the node id its first start wrote, alone when no transport is there
- [x] SocialismusLink on a server writes a secret and keeps trying to reach the proxy, saying so once
- [x] With SocialismusLink, a proxy and its servers share chat without Redis, the proxy listening on a port Anvil reserves
- [x] With SocialismusChannelizer, a global message reaches the players on the other backend and a local one stays on its own
- [x] With SocialismusRedis, the same holds through Redis, and the sender's name and format arrive as written
- [x] A backend keeps chatting while Redis hangs, and shares chat again once Redis answers
- [ ] A backend that starts later receives the messages sent after it joined
- [x] A proxy and its servers, all with SocialismusNetwork, show each message exactly once
- [x] A server without Socialismus behind such a proxy still gets its players' chat handled by the proxy
- [x] A server with `chat.owner: PROXY` leaves its players' chat to the proxy and shows each message once
- [x] A server that stops answering hands its players' chat to the proxy within the lease time, and takes it back once it answers
- [x] A server behind a proxy learns its name, so a server requirement judges its players
- [x] `/msg` reaches a player on another backend, and the sender sees it once that backend confirms
- [ ] A private message to a player on no backend fails once `messaging.confirmation` has passed

## modules

Socialismus's module system. Each module built for Socialismus has a suite of its own below;
SocialismusRedis and SocialismusChannelizer, which carry chat between servers, are in `sync`.

- [x] The jars in `modules/` load, each logs that it loaded, and `/socialismus debug` lists them
- [x] A module that needs another Socialismus version is refused with a reason
- [x] A module that does not list the running platform is refused with a reason
- [x] A module reaches the exports of a module it depends on, never the rest of that module, and the
  classes of a plugin it depends on
- [x] A module stops before the module it depends on
- [x] `/socialismus reload` reloads the modules' configuration

## chirper

- [x] Chirper broadcasts the chat announcements of an announcer on its schedule
- [x] `/socialismus announce` sends an announcement right away
- [x] An announcer's requirements limit who receives its announcements
- [x] Chirper's title, action-bar, boss-bar and sound announcements arrive

## dialogue

- [x] `/msg` delivers a private message and confirms it to the sender
- [x] `/reply` answers the last conversation partner
- [x] Messaging an offline player fails with a reason
- [x] `/reply` without a conversation fails with a reason
- [x] A private message takes the last format whose requirements its participants meet
- [x] `messaging.requirements` refuse a message to a player they exclude
- [x] A refused sender is told why in Dialogue's wording for the participant that failed, such as the
  recipient's missing permission, and gets `notAllowed` only when nothing words it
- [x] A private message is shown as its sender wrote it, never read as formatting such as a click
- [x] The recipient gets the alert of their format: a sound, an action bar and a title
- [x] `/message <player>` without a message sends chat to that player until the same command ends it
- [x] `/msgtoggle` turns private messages off, except from senders who meet `control.toggle.bypass`
- [x] `/ignore <player>` keeps that player's private messages away, and `/ignore` lists the ignored players
- [x] With `control.ignore.includesChat`, ignoring a player hides their chat as well
- [x] `/socialspy` shows staff the private messages of others; privacy mode keeps spies out
- [ ] Toggles and ignores outlive a restart with SocialismusDatabase
- [ ] A player hidden from the sender, such as by a vanish plugin, looks offline to them **needs a vanish plugin on the test server**
- [ ] Clicking a received message suggests a reply to its sender **needs Anvil**: players see messages as plain text, without their click events
- [ ] `/reply` to a conversation older than `messaging.reply.timeout` lists the recent conversations

## essentials

Essentials keeps its feature framework, but has no feature since private messages became Dialogue.

- [x] `/socialismus features` says that no feature runs

## bubbler

- [x] Bubbler shows a bubble above a player who chats or runs `/socialismus bubble`, with each render strategy

## database

SocialismusDatabase on a Paper server; its tests against PostgreSQL and MariaDB run in containers in
`integrationTest`.

- [x] SocialismusDatabase loads its SQLite driver, opens the database and registers it for the other modules
- [ ] A module keeps its data in the database once SocialismusDatabase registers it, and in memory before

## upgrade

Start from the data directory an earlier version left.

- [x] The 2.0.0-RC6 data directory, with edited values, starts; `settings` records the latest version, no other document carries `_version`, and the edits stay **first**
- [ ] A directory of the development builds after RC6, which wrote `_version: 1` into every document, starts and loses those versions
- [ ] The RC6 directories of Bubbler, Chirper, Essentials and SocialismusRedis migrate when their module loads; Dialogue starts fresh
- [ ] A directory already at the latest version starts without running a migration
- [ ] A migration that fails stops the startup and names the document
