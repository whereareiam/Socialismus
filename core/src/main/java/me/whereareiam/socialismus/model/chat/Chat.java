package me.whereareiam.socialismus.model.chat;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public String id;
    public ChatUse usage = new ChatUse();
    public String messageFormat;
    public List<String> hoverFormat = new ArrayList<>();
    public ChatRequirements requirements = new ChatRequirements();
}
