package me.whereareiam.socialismus.model.style;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code styles.yml} in the data directory: the styles players may write with, which chats and modules refer to by
 * id. Legacy codes such as {@code &c} work in what players write when {@code serialization.enableLegacyColors} of
 * {@code settings.yml} is on, as the tags they stand for and only where the style allows those tags.
 */
@Getter
@Setter
@ToString
public class StylesConfig {
	/**
	 * The styles. Kept as written, so a file of one's own takes none of the default styles.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private List<Style> styles = new ArrayList<>();
}
