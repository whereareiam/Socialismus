package me.whereareiam.socialismus.config.feature.swapper;

import me.whereareiam.socialismus.feature.swapper.model.Swapper;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

public class SwapperConfig extends YamlSerializable {
    private static final SerializerConfig SWAPPERS = new SerializerConfig.Builder().build();

    @Comment(
            value = {
                    @CommentValue(" Example configuration:"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" swappers:"),
                    @CommentValue("   - enabled: true"),
                    @CommentValue("     settings:"),
                    @CommentValue("       permission: \"privilege.2\""),
                    @CommentValue("       direct-search: true"),
                    @CommentValue("       random-content: true"),
                    @CommentValue("     contents:"),
                    @CommentValue("       - \"example1\""),
                    @CommentValue("     placeholders:"),
                    @CommentValue("       - \"{example}\""),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" Explanation:"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" enabled -                 Allows you to toggle the capability of this swapper."),
                    @CommentValue("                           **It should be specified, you can use true or false**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" settings.permission -     Allows you to specify the required permission to use."),
                    @CommentValue("                           **It is not necessary to specify**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" settings.direct-search -  Changes the placeholder search mode."),
                    @CommentValue("                           true -  Looks for a placeholder without paying attention to the characters nearby."),
                    @CommentValue("                                   Enables support for regex."),
                    @CommentValue("                           false - Looks for a placeholder that is surrounded by a space in the surrounding area."),
                    @CommentValue("                           **It should be specified, you can use true or false**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" settings.random-content - If enabled, any message from the list will be taken, if disabled, the first message"),
                    @CommentValue("                           will be taken."),
                    @CommentValue("                           **It should be specified, you can use true or false**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" contents -                A list with messages of what the placeholder will be replaced with."),
                    @CommentValue("                           The random-content option affects this list directly."),
                    @CommentValue("                           **It should be specified, you can use placeholders, letters and numbers**"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" placeholders -            List of placeholders to be searched. You can use regex if direct-search is enabled"),
                    @CommentValue("                           and there is a $ symbol before the pattern."),
                    @CommentValue("                           **It should be specified, you can use regex, letters and numbers**"),
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
            },
            at = Comment.At.PREPEND
    )
    public List<Swapper> swappers = new ArrayList<>();

    public SwapperConfig() {
        super(SwapperConfig.SWAPPERS);
    }
}
