package me.whereareiam.socialismus.module.mentionator.common.config.dynamic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * A file of {@code tags/}.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagsConfig {
	private List<Tag> tags = new ArrayList<>();
}
