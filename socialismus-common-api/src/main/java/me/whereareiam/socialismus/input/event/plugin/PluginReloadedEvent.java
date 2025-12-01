package me.whereareiam.socialismus.input.event.plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.input.event.base.Event;

/**
 * Event that is fired when the Socialismus plugin is being reloaded.
 * This event is cancellable and can be used to prevent or modify the reload process.
 *
 * <p>The event is triggered before the actual reload occurs, allowing addon developers
 * to perform cleanup operations or prevent the reload if necessary.</p>
 *
 * <p>Being a {@link CancellableEvent}, the reload process can be cancelled by setting
 * the cancelled flag to true.</p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class PluginReloadedEvent implements Event, CancellableEvent {
    /**
     * Flag indicating whether the reload event has been cancelled.
     */
    private boolean cancelled;
}