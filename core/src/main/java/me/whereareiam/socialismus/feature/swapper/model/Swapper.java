package me.whereareiam.socialismus.feature.swapper.model;

import java.util.ArrayList;
import java.util.List;

public class Swapper {
    public boolean enabled = true;
    public SwapperSettings settings = new SwapperSettings();
    public List<String> contents = new ArrayList<>();
    public List<String> placeholders = new ArrayList<>();
}
