---
version: 2.0.0
---

# Update 2.0.0 - [Short subtitle]

[1-2 sentence summary of the release.]

## Upgrading

* Update from 2.0.0-RC6.
* Addons: `PluginBootstrappedEvent`, `PluginReadyEvent`, `PluginInitializedEvent` and `PluginShutdownEvent` were removed; `PluginReloadedEvent` is now called after `/socialismus reload` and can no longer be cancelled. Rebuild what your addon derives from Socialismus documents on it.
