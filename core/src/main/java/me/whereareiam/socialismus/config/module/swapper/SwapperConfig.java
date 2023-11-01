package me.whereareiam.socialismus.config.module.swapper;

import me.whereareiam.socialismus.module.swapper.model.Swapper;
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
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" SWAPPER DOCUMENTATION"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" - whereareiam:"),
                    @CommentValue(" Preface, it was described earlier that you can use this to create shortcuts for"),
                    @CommentValue(" messages or emojis. But all functionality is not limited to that, you can create"),
                    @CommentValue(" much more, it only depends on your imagination."),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" If you have any suggestions for improvements to this module, please feel free to"),
                    @CommentValue(" post a suggestion on the github page. https://github.com/whereareiam/Socialismus"),
                    @CommentValue(type = CommentValue.Type.TEXT),
                    @CommentValue(" Here you can create as many Swappers as you like."),
                    @CommentValue(" swappers:"),
                    @CommentValue("   Swapper toggle allows you to specify if it is enabled and if it is usable for players,"),
                    @CommentValue("   a good option if you need to temporarily disable it instead of deleting it."),
                    @CommentValue("   - enabled: true"),
                    @CommentValue("     Swapper's settings allow you to customize how it will behave."),
                    @CommentValue("     settings:"),
                    @CommentValue("       Here you can specify what permission a player needs to use this swapper. If the"),
                    @CommentValue("       required permission is not given, the player will get a message if it is not set to null."),
                    @CommentValue("       Set 'permission: \"\"' to disable permission check."),
                    @CommentValue("       permission: \"privilege.2\""),
                    @CommentValue("       The Direct Search option controls how the search works. If you set it to true, it will"),
                    @CommentValue("       search exactly for this placeholder and will not care if it is in the middle of a text"),
                    @CommentValue("       or not. "),
                    @CommentValue("       "),
                    @CommentValue("       EXAMPLE"),
                    @CommentValue("         Our text is: \"I like bananas\"."),
                    @CommentValue("         Our placeholder is: \"an\"."),
                    @CommentValue("         The content is: \"wow!\""),
                    @CommentValue("         In the end, our text will change to this:"),
                    @CommentValue("         \"I like bwow!wow!as\"."),
                    @CommentValue("       direct-search: true"),
                    @CommentValue("       The Random content option controls the behavior of the content list. If you set it to"),
                    @CommentValue("       true, it will grab any value from content and insert it instead of a placeholder. If"),
                    @CommentValue("       you set it to true, it will grab any value from content and insert it instead of a"),
                    @CommentValue("       placeholder. If you set it to false, the first content will be grabbed and inserted"),
                    @CommentValue("       instead of the placeholder."),
                    @CommentValue("       If you want to use regex patterns in your placeholder, change this option to true and"),
                    @CommentValue("       add $ to the beginning of your regex pattern in the placeholder list."),
                    @CommentValue("       random-content: true"),
                    @CommentValue("     Content allows you to specify a list of content to replace a placeholder with."),
                    @CommentValue("     You can use PlaceholderAPI placeholders and MiniMessage formatting."),
                    @CommentValue("     content:"),
                    @CommentValue("       - \"example1\""),
                    @CommentValue("       - \"%player_name% is <gold><bold>amazing<reset>!\""),
                    @CommentValue("       - \"%itemsadder_my_super_custom_symbol%\""),
                    @CommentValue("     Here you can specify the content that will be displayed when the mouse is hovered"),
                    @CommentValue("     over the replaced placeholder."),
                    @CommentValue("     You can use PlaceholderAPI placeholders and MiniMessage formatting."),
                    @CommentValue("     content-hover:"),
                    @CommentValue("       - \"<green>Oh look at that!\""),
                    @CommentValue("     A list of placeholders that will be replaced with content from the list above."),
                    @CommentValue("     placeholders:"),
                    @CommentValue("       - \"{example}\""),
                    @CommentValue("       - \"$(banana|bananas)\""),
                    @CommentValue("       - \":lol:\""),
                    @CommentValue(type = CommentValue.Type.NEW_LINE),
            },
            at = Comment.At.PREPEND
    )
    public List<Swapper> swappers = new ArrayList<>();

    public SwapperConfig() {
        super(SwapperConfig.SWAPPERS);
    }
}
