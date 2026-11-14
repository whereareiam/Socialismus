package me.whereareiam.socialismus.module.mentionator.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.module.mentionator.common.notify.SoundCue;
import me.whereareiam.socialismus.module.mentionator.common.notify.TagDelivery;

import java.util.List;

/**
 * What travels on {@code mentionator:tags} to the node of a tagged player: the notifications of a tag to deliver
 * there, or the sounds a proxy hands to the server the player is on.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TagRecord {
	/**
	 * The node that hosts the player.
	 */
	private String target;

	/**
	 * The player's unique id.
	 */
	private String player;

	/**
	 * What tagged them, to notify them; {@code null} when only sounds are handed over.
	 */
	private TagDelivery delivery;

	/**
	 * The sounds a proxy handed over; empty with a delivery.
	 */
	private List<SoundCue> sounds;
}
