package me.whereareiam.socialismus.config.module.announcer;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.announcer.Announcer;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class AnnouncerConfig extends YamlSerializable {
    public List<Announcer> announcers = new ArrayList<>();
}
