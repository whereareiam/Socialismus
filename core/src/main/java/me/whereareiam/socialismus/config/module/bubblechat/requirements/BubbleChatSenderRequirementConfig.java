package me.whereareiam.socialismus.config.module.bubblechat.requirements;

import com.google.inject.Singleton;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class BubbleChatSenderRequirementConfig {
    public String usePermission;

    @Comment(
            value = {
                    @CommentValue(" This requirement allows you to specify how many symbols are required to"),
                    @CommentValue(" display a bubble message. If the condition is not met, the bubble message"),
                    @CommentValue(" will not be displayed.")
            },
            at = Comment.At.PREPEND
    )
    public int symbolCountThreshold = 5;

    public List<String> worlds = new ArrayList<>();
}
