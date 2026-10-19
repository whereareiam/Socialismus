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
* Private messages moved out of Essentials into the new Dialogue module: install the Dialogue jar for `/msg` and `/reply`; Essentials no longer ships. Dialogue starts with its own files in `modules/Dialogue/`, so set again what you changed in `modules/Essentials/features/dialogue/`.
* Addons: a `RequirementValidation` answers through `evaluate`, which returns a `RequirementResult`, instead of a boolean from `check`; return `RequirementResult.of(met)`, or `RequirementResult.denied(RequirementDenial.of(key, failure, placeholders))` so the feature that checked the requirement can tell the player why. `RequirementEvaluatorService.check` still answers yes or no, `evaluate` gives the denial, and `RequirementReasonService` words it.
* Chat history's `permission` and `bypassPermission` in `chat/settings.yml` become the requirement groups `requirements` and `bypassRequirements`, so who may clear messages and whose messages stay can depend on anything a requirement checks. The first start turns your permissions into groups that ask for them; nothing to do by hand. Addons read `ChatHistorySettings.getRequirements()` and `getBypassRequirements()` instead of the permission strings.
* Addons: `module.json` changes shape. `socialismus` names the Socialismus versions a module is built for instead of a `BASE` dependency; `platforms` lists where it runs, each with an optional version pattern, such as `{"type": "PAPER", "version": "26\\.1(\\..*)?"}`, instead of `supportedPlatforms` and `supportedVersions`; a dependency on another plugin is `PLATFORM` instead of `PLATFORM_COMPONENT` and reaches all of that plugin's classes, so `apiPackages` is gone. A module shares packages with the modules that depend on it through `exports`, and every dependency may be `optional`.
