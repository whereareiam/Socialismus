package me.whereareiam.socialismus.config.feature.bubblechat;

import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

public class BubbleChatConfig extends YamlSerializable {
    private static final SerializerConfig BUBBLECHAT = new SerializerConfig.Builder().build();

    public BubbleChatSettingsConfig settings = new BubbleChatSettingsConfig();
    public BubbleChatFormatConfig format = new BubbleChatFormatConfig();
    public BubbleChatRequirementConfig requirements = new BubbleChatRequirementConfig();

    public BubbleChatConfig() {
        super(BubbleChatConfig.BUBBLECHAT);
    }
}
