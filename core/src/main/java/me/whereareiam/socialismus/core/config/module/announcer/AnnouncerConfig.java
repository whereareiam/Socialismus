package me.whereareiam.socialismus.core.config.module.announcer;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.announcer.Announcer;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class AnnouncerConfig extends YamlSerializable {

	@Comment(
			value = {
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" ANNOUNCER DOCUMENTATION:"),
					@CommentValue(type = CommentValue.Type.TEXT),
					@CommentValue(" Here you can create as many Announcers as you like."),
					@CommentValue(" announcers:"),
					@CommentValue("   - enabled: true"),
					@CommentValue("     Here you can specify how often the announcer will send an announcement."),
					@CommentValue("     interval: 60"),
					@CommentValue("     Here you can specify how the announcer will select the messages."),
					@CommentValue("     Possible values:"),
					@CommentValue("       - RANDOM (selects a random message)"),
					@CommentValue("       - SEQUENTIAL (selects the next message in the list)"),
					@CommentValue("     selection-type: SEQUENTIAL"),
					@CommentValue("     Here you can specify the announcement ID that the announcer will send."),
					@CommentValue("     announcements:"),
					@CommentValue("       - \"example0\""),
					@CommentValue("       - \"example1\""),
					@CommentValue(type = CommentValue.Type.NEW_LINE),
			},
			at = Comment.At.PREPEND
	)
	public List<Announcer> announcers = new ArrayList<>();
}
