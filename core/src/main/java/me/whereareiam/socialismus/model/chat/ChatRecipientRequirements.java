package me.whereareiam.socialismus.model.chat;

import java.util.List;

public class ChatRecipientRequirements {
    public int radius = -1;
    public boolean seeOwnMessage = true;
    public String seePermission;
    public List<String> worlds = List.of("world");
}
