package me.whereareiam.socialismus.config.message.command;

import java.util.List;

public class MainCommandMessagesConfig {
    public String description = "Help command";
    public String commandFormat = "  <yellow>{command}<gray>{subCommand} <dark_gray>- <white>{description}";
    public List<String> helpFormat = List.of(
            " ",
            " <green><bold>Command help <reset><gray>[List of commands]",
            "{commands}",
            " "
    );
}
