package me.whereareiam.socialismus.config.message;

import java.util.List;

public class MainCommandMessagesConfig {
    public String description = "Help command";
    public String commandHelpFormat = "  <yellow>{command}<gray>{subCommand} <dark_gray>- <white>{description}";
    public List<String> helpFormat = List.of(
            " ",
            " <green><bold>Command help <reset><gray>[List of commands]",
            "{commands}",
            " "
    );
}
