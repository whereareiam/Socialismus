package me.whereareiam.socialismus.config.feature.bubblechat;

import com.google.inject.Singleton;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

@Singleton
public class BubbleChatConfig extends YamlSerializable {
    private static final SerializerConfig BUBBLECHAT = new SerializerConfig.Builder().build();

    public BubbleChatSettingsConfig settings = new BubbleChatSettingsConfig();
    public BubbleChatFormatConfig format = new BubbleChatFormatConfig();
    public BubbleChatRequirementConfig requirements = new BubbleChatRequirementConfig();

    public BubbleChatConfig() {
        super(BubbleChatConfig.BUBBLECHAT);
    }
}
