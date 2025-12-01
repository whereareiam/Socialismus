package me.whereareiam.socialismus.output.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify cooldown settings for command execution.
 * When applied to a command method, it defines the cooldown period
 * between consecutive executions of that command.
 *
 * <p>The cooldown value is specified as a string that can be parsed
 * into a duration (e.g., "5s" for 5 seconds, "1m" for 1 minute).</p>
 *
 * <p>Example usage:</p>
 * {@code @CommandCooldown("30s")}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandCooldown {
    /**
     * The cooldown duration value.
     *
     * @return a string representing the cooldown duration
     */
    String value();
}