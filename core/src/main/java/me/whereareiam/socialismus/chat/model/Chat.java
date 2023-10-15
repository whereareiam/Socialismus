package me.whereareiam.socialismus.chat.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public String id;
    public String seePermission;
    public String writePermission;
    public String chatSymbol;
    public int radius;
    public String messageFormat;
    public List<String> hoverFormat = new ArrayList<>();
    public List<String> worlds = new ArrayList<>();
}
