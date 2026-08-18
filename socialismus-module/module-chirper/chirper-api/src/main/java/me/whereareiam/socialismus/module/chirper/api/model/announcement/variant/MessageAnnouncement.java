package me.whereareiam.socialismus.module.chirper.api.model.announcement.variant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.AnnouncementContent;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class MessageAnnouncement extends AnnouncementContent {
	private List<String> messages;
}
