package me.whereareiam.socialismus.module.mentionator.common.notify;

import org.jetbrains.annotations.NotNull;

/**
 * A sound to play to a player, handed by a proxy to the server the player is on.
 *
 * @param sound  the sound's key
 * @param volume how loud
 * @param pitch  how high
 */
public record SoundCue(@NotNull String sound, float volume, float pitch) {
}
