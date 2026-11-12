package me.whereareiam.socialismus.module;

import me.whereareiam.chassis.Extension;

/**
 * The main class of a Socialismus module, named by {@code main} in its {@code module.yml}.
 *
 * <p>Socialismus makes the instance through its injector, so its constructor may take Socialismus's services, then
 * enables it once every module it depends on runs, and disables it before them. What the module registers through
 * its context, and the resources of a {@link me.whereareiam.socialismus.service.resource.ResourceProvider}, are removed
 * when it stops.</p>
 *
 * <pre>{@code
 * public final class Chirper implements SocialisticModule {
 *     @Override
 *     public void enable(ModuleContext context) {
 *         Path folder = context.dataFolder();
 *         ...
 *     }
 *
 *     @Override
 *     public void disable() {
 *         ...
 *     }
 * }
 * }</pre>
 */
public interface SocialisticModule extends Extension<ModuleContext> {
}
