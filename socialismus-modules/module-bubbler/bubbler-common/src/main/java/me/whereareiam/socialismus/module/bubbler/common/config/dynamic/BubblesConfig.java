package me.whereareiam.socialismus.module.bubbler.common.config.dynamic;

import lombok.Getter;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BubblesConfig {
    private List<Bubble> bubbles = new ArrayList<>();
}
