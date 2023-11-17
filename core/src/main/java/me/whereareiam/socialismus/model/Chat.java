package me.whereareiam.socialismus.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public String id;
    public String chatSymbol;
    public int radius;
    public String messageFormat;
    public List<String> hoverFormat = new ArrayList<>();
    public Requirement requirement = new Requirement();
}
