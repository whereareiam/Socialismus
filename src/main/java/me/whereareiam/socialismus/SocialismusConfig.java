package me.whereareiam.socialismus;

import com.google.inject.AbstractModule;
import me.whereareiam.socialismus.config.SettingsConfig;

public class SocialismusConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(SettingsConfig.class).toInstance(new SettingsConfig());
    }
}