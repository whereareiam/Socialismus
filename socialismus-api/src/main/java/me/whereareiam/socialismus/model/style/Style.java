package me.whereareiam.socialismus.model.style;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;

import java.util.ArrayList;
import java.util.List;

/**
 * A style of {@code styles.yml}: the formatting tags a player may use in what they write, such as in chat. A place
 * where players write lists the styles it accepts by id, and the last of them whose {@link #getRequires() requires}
 * the writer meets is used, like the formats of a chat:
 *
 * <pre>{@code
 * styles:
 *   - id: "basic"
 *     tags: ["color", "bold", "italic"]
 *     requires:
 *       - permission: "socialismus.style.basic"
 *   - id: "vip"
 *     tags: ["*"]
 *     excluded: ["obfuscated", "font"]
 *     requires:
 *       - permission: "socialismus.style.vip"
 * }</pre>
 *
 * <p>Tags that act on whoever reads the message, or make the server look something up, are never read from what a
 * player writes, whatever a style lists: {@code click}, {@code insert}, {@code nbt}, {@code selector},
 * {@code score}, {@code newline} and {@code reset}.</p>
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Style {
	/**
	 * The name places refer to the style by.
	 */
	private String id;

	/**
	 * The tags the style allows, by name, such as {@code bold}, or by group: {@code decorations},
	 * {@code gradients}, or {@code *} for every tag a player may use.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private List<String> tags = new ArrayList<>();

	/**
	 * The tags taken out of {@link #getTags() tags} again, by name or group.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private List<String> excluded = new ArrayList<>();

	/**
	 * What the writer must meet for the style, checked with the part sender. Kept as written; an empty list holds for
	 * everyone.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements requires = Requirements.none();
}
