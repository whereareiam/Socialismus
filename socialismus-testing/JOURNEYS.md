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

Players send chat and commands and receive chat as plain text, including action bars sent as chat.
Anvil also reports the command tree a player holds, the server a player is on, kicks, console
output, and lets players move and use items. It does not record titles, action bars sent on their
own, boss bars, sounds or entities. Journeys that need those are marked **needs Anvil** below and
wait for an Anvil capability or an agent operation.

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
nobody on Velocity, and the BUKKIT jar does not start on Spigot. The reload and Spigot journeys stay
disabled until their fixes land.

## chat

One Paper server unless stated otherwise.

- [x] A message reaches every player in the format of the chat it matched **first**
- [x] A symbol trigger picks its chat and is stripped from the message, such as `!` for global chat **first**
- [x] Local chat reaches only the players within its radius **first**
- [ ] A permission requirement decides who may send in a chat and who receives it
- [ ] A world requirement keeps a chat to the players of its worlds
- [ ] A placeholder or message requirement selects the chat
- [ ] The chat with the highest priority wins when several match
- [ ] A chat shows each recipient the format whose requirements they meet
- [ ] A command trigger and a regex trigger send to their chat
- [ ] A disabled chat is skipped
- [ ] A message that matches no chat gets the no-chat message
- [ ] Chats from several files in `chats/` are all loaded
- [ ] MiniMessage and legacy serialization render the same format
- [ ] PlaceholderAPI placeholders are filled in formats when PlaceholderAPI is installed

## commands

- [x] `/socialismus help` lists the commands the player may use **first**
- [ ] `/socialismus reload` applies a changed format without a restart **first**, added but disabled until the lifecycle refactor fixes the reload order
- [ ] `/socialismus clear` clears the chat of the players
- [ ] `/socialismus debug` reports the platform and version
- [ ] A player without permission neither sees nor runs the administrator commands

## platform

- [x] The PAPER jar starts on the latest Paper and a player chats **first**
- [ ] The BUKKIT jar starts on the latest Spigot and a player chats **first**, added but disabled until the jar starts on Spigot
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

Two Paper backends behind a Velocity proxy.

- [ ] With SocialismusChannelizer, a global message reaches the players on the other backend and a local one stays on its own
- [ ] With SocialismusRedis, the same holds through Redis (needs Docker)
- [ ] Sender and recipients keep their names and formats on the other backend
- [ ] A backend that starts later receives the messages sent after it joined

## modules

- [ ] The jars in `modules/` load and the banner lists them
- [ ] A module that needs another Socialismus version is refused with a reason
- [ ] `/socialismus reload` reloads the modules' configuration
- [ ] Chirper broadcasts the chat announcements of an announcer on its schedule
- [ ] `/socialismus announce` sends an announcement right away
- [ ] An announcer's requirements limit who receives its announcements
- [ ] Chirper's title, action-bar, boss-bar and sound announcements arrive **needs Anvil**
- [ ] Essentials: `/msg` delivers a private message and confirms it to the sender
- [ ] Essentials: `/reply` answers the last conversation partner
- [ ] Essentials: messaging an offline player fails with a reason
- [ ] Essentials: `/socialismus features` lists the features and their state
- [ ] Essentials: private messages reach a player on another backend
- [ ] Bubbler shows a bubble above a player who chats or runs `/socialismus bubble`, with each render strategy **needs Anvil**

## upgrade

Start from the data directory an earlier version left.

- [x] The 2.0.0-RC6 data directory, with edited values, starts; `settings` records the latest version, no other document carries `_version`, and the edits stay **first**
- [ ] A directory of the development builds after RC6, which wrote `_version: 1` into every document, starts and loses those versions
- [ ] The RC6 directories of Bubbler, Chirper, Essentials and SocialismusRedis migrate when their module loads
- [ ] A directory already at the latest version starts without running a migration
- [ ] A migration that fails stops the startup and names the document
