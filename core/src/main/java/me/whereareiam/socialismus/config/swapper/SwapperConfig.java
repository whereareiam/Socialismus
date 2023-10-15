package me.whereareiam.socialismus.config.swapper;

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
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
            },
            at = Comment.At.PREPEND
    )
    public List<Swapper> swappers = new ArrayList<>();

    public SwapperConfig() {
        super(SwapperConfig.SWAPPERS);
    }
}
