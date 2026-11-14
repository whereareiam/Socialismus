package me.whereareiam.socialismus.module.mentionator.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorMessages;
import me.whereareiam.socialismus.module.mentionator.api.type.Mode;

import java.util.EnumMap;
import java.util.Map;

@Singleton
public class MentionatorMessagesDefaults implements DefaultsProvider<MentionatorMessages> {
	@Override
	public MentionatorMessages supply(MentionatorMessages messages) {
		MentionatorMessages.Mentions mentions = new MentionatorMessages.Mentions();
		mentions.setOverview("{prefix}<white>You hear <gray>{mode}</gray>. Tags you may use: {tags}");
		mentions.setTag("<hover:show_text:'<gray>Written {written}'><aqua>{tag}</aqua></hover>");
		mentions.setSeparator("<gray>, ");
		mentions.setNoTags("<gray>none");
		mentions.setChanged("{prefix}<white>You hear <gray>{mode}</gray> now.");
		Map<Mode, String> modes = new EnumMap<>(Mode.class);
		modes.put(Mode.ALL, "every tag");
		modes.put(Mode.DIRECT, "only tags of your name");
		modes.put(Mode.NONE, "no tags");
		mentions.setModes(modes);
		messages.setMentions(mentions);

		MentionatorMessages.Limits limits = new MentionatorMessages.Limits();
		limits.setPerMessage("{prefix}<white>Only the first <gray>{limit}</gray> tags of a message count.");
		messages.setLimits(limits);

		return messages;
	}
}
