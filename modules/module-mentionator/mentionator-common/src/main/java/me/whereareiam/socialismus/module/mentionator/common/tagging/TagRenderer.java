package me.whereareiam.socialismus.module.mentionator.common.tagging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.TagFormat;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.TagUse;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.Tagging;
import me.whereareiam.socialismus.module.mentionator.common.condition.Catalog;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Renders a tag in one of its formats. A rendered tag is marked with the text the sender wrote as its insertion, so
 * each viewer's format can take its place in the message.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TagRenderer {
	/**
	 * Renders a tag as every viewer sees it unless a later format fits them: the first format the sender met whose
	 * {@code reaches} is empty, or the tag as written. It goes into the message itself, so what copies the message,
	 * such as a chat bubble, shows the tag so.
	 *
	 * @param catalog the catalog the tag is from
	 * @param tag     the tag
	 * @param use     where it was written
	 * @return the marked tag
	 */
	public static @NotNull Component plain(@NotNull Catalog catalog, @NotNull Tag tag, @NotNull TagUse use) {
		Integer format = use.formats().stream()
				.filter(index -> catalog.reachesAnyone(tag, index))
				.findFirst()
				.orElse(null);

		return format == null ? written(use) : render(tag, tag.getFormats().get(format), use, null);
	}

	/**
	 * Renders a tag for one viewer: the last format of those the sender met whose {@code reaches} the viewer meets,
	 * or the tag as written.
	 *
	 * @param catalog the catalog the tag is from
	 * @param use     where it was written
	 * @param viewer  the viewer
	 * @param message the message
	 * @return the marked tag, {@code null} when this server does not know the tag
	 */
	public static @Nullable Component forViewer(
			@NotNull Catalog catalog,
			@NotNull TagUse use,
			@NotNull SocialismusPlayer viewer,
			@NotNull ChatMessage message
	) {
		Tag tag = catalog.tag(use.tag()).orElse(null);
		if (tag == null) return null;

		ChatMessage alone = alone(message, use);
		List<Integer> formats = use.formats();
		for (int index = formats.size() - 1; index >= 0; index--) {
			int format = formats.get(index);
			if (format >= tag.getFormats().size()) continue;
			if (catalog.reaches(tag, format, viewer, message.getSender(), alone))
				return render(tag, tag.getFormats().get(format), use, viewer);
		}

		return written(use);
	}

	/**
	 * Returns whether a component is the mark of a tag: the text the sender wrote as its insertion, whether or not a
	 * serializer merged the mark with what it holds.
	 *
	 * @param component the component
	 * @param use       the tag
	 * @return whether the component marks it
	 */
	public static boolean marks(@NotNull Component component, @NotNull TagUse use) {
		return Objects.equals(component.insertion(), use.written());
	}

	private static Component render(Tag tag, TagFormat format, TagUse use, @Nullable Actor viewer) {
		Component rendered = Serializer.serialize(SerializerContent.builder()
				.receiver(viewer)
				.message(format.getFormat() == null ? use.written() : format.getFormat())
				.placeholder("match", escape(use.match()))
				.placeholder("tag", tag.getId())
				.build());

		return mark(use, rendered);
	}

	private static Component written(TagUse use) {
		return mark(use, Component.text(use.written()));
	}

	private static Component mark(TagUse use, Component content) {
		return Component.text().insertion(use.written()).append(content).build();
	}

	// The message with only this tag as its tagging, so mentionator:targeted in a format holds for whom this tag tags.
	private static ChatMessage alone(ChatMessage message, TagUse use) {
		ChatMessage alone = ChatMessage.builder()
				.id(message.getId())
				.sender(message.getSender())
				.recipients(message.getRecipients())
				.content(message.getContent())
				.chat(message.getChat())
				.trigger(message.getTrigger())
				.build();
		alone.setAttribute(Tagging.ATTRIBUTE, new Tagging(List.of(use)));

		return alone;
	}

	/**
	 * Keeps a value from being read as formatting, such as a placeholder value holding {@code <red>}.
	 *
	 * @param value the value
	 * @return the value as text
	 */
	public static @NotNull String escape(@NotNull String value) {
		return value.replace("\\", "\\\\").replace("<", "\\<");
	}
}
