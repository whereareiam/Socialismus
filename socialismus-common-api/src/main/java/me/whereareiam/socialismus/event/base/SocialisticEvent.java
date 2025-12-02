package me.whereareiam.socialismus.event.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a Socialismus event handler.
 * Methods annotated with this annotation will be registered as event listeners
 * in the Socialismus event system.
 *
 * <p>Event handlers can specify their execution order using the {@link EventOrder}
 * parameter. By default, events use {@link EventOrder#NORMAL} priority.</p>
 *
 * Example usage:
 * <pre>
 * &#64;SocialisticEvent(EventOrder.HIGH)
 * public void onPlayerChat(ChatEvent event) {
 *     // Handle chat event
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SocialisticEvent {
    /** The order in which this event handler should be executed */
    EventOrder value() default EventOrder.NORMAL;
}