package me.whereareiam.socialismus.module.chirper.common.config.dynamic;

import lombok.Getter;
import me.whereareiam.socialismus.module.chirper.api.model.announcer.Announcer;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AnnouncersConfig {
    private List<Announcer> announcers = new ArrayList<>();
}
