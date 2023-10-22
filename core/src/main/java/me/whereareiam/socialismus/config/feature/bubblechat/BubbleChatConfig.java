package me.whereareiam.socialismus.config.feature.bubblechat;

import com.google.inject.Singleton;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

@Singleton
public class BubbleChatConfig extends YamlSerializable {
    private static final SerializerConfig BUBBLECHAT = new SerializerConfig.Builder().build();

    @Comment(
            value = {
                    @CommentValue(" Settings section allows you to customise how the TextDisplay entity behaves."),
            },
            at = Comment.At.PREPEND
    )
    public BubbleChatSettingsConfig settings = new BubbleChatSettingsConfig();

    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" Format section allows you to change the design as it would be displayed."),
            },
            at = Comment.At.PREPEND
    )
    public BubbleChatFormatConfig format = new BubbleChatFormatConfig();

    @Comment(
            value = {
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
                    @CommentValue(" In the Requirements section, you can specify what your players need in order"),
                    @CommentValue(" to be able to send or receive a bubble message."),
            },
            at = Comment.At.PREPEND
    )
    public BubbleChatRequirementConfig requirements = new BubbleChatRequirementConfig();

    public BubbleChatConfig() {
        super(BubbleChatConfig.BUBBLECHAT);
    }
}
