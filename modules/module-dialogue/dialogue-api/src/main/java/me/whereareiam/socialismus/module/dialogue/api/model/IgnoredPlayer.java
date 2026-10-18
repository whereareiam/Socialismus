package me.whereareiam.socialismus.module.dialogue.api.model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A player someone ignores: their unique id, which the ignoring follows, and the name they had when ignored.
 */
@Value
public class IgnoredPlayer {
	@NotNull UUID uniqueId;
	@NotNull String name;
}
