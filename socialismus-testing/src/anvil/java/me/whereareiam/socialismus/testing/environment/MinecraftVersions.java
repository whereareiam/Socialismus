package me.whereareiam.socialismus.testing.environment;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Limits a journey to the Minecraft versions it applies to, such as text display bubbles from 1.19.4; at other
 * versions it is skipped before its network starts.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MinecraftVersionCondition.class)
public @interface MinecraftVersions {
	/**
	 * The first version the journey runs at, or blank for every older one.
	 */
	String since() default "";

	/**
	 * The first version the journey no longer runs at, or blank for every newer one.
	 */
	String before() default "";
}
