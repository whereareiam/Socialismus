---
version: 2.0.0
---

# Update 2.0.0 - [Short subtitle]

[1-2 sentence summary of the release.]

## Upgrading

* Update from 2.0.0-RC6.
* Addons: `PluginBootstrappedEvent`, `PluginReadyEvent`, `PluginInitializedEvent` and `PluginShutdownEvent` were removed; `PluginReloadedEvent` is now called after `/socialismus reload` and can no longer be cancelled. Rebuild what your addon derives from Socialismus documents on it.
* The configuration has one document per topic now: `sync.yml` and `updater.yml` are split out of `settings.yml`, the command behaviour moves to `commands.yml`, and the chat documents and chats move from `chats/` to `chat/`, with the chats in `chat/list/`. The first start moves every value you changed and backs up each file it touches to `.strata/backup/`; nothing to do by hand.
* Settings renamed on the way: `misc.disableJoinNotification` and `misc.disableQuitNotification` become `vanilla.joinMessage` and `vanilla.quitMessage`, which say whether the message is shown; `misc.vanillaSending` becomes `vanilla.sending`; the unused `misc.allowLegacyParsing` is removed; `updater.interval` is a duration such as `"1h"`.
* Addons: `Settings` no longer holds the sync, updater and command settings; read the `Sync`, `Updater` and `Commands` documents instead, and `ChatSync` for what chats do when synced.
* Addons: resources are named by typed `ResourceKey`s instead of `ResourceType`; Socialismus's own are in `Constants.Resources`, and a module defines its own. `ResourceProvider` returns `ProvidedResource`s, `ResourceRegistry.register` reports `KEY_TAKEN` instead of keeping the first resource silently, and `ResourceRegisteredEvent` and `ResourceUnregisteredEvent` announce each change. A `module.json` requires a resource by its key: `"resource": "socialismus:sync"` instead of `"type": "SYNC"`.
* Addons: Socialismus no longer has a database resource or `DatabaseService`; the database is SocialismusDatabase's, with its own API.
