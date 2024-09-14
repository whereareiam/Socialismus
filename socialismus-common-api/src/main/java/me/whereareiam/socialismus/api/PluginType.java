package me.whereareiam.socialismus.api;

import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public enum PluginType {
    UNKNOWN, MODERN, BUKKIT, PAPER, VELOCITY;

    @Setter
    private static PluginType pluginType = UNKNOWN;

    public static PluginType getExactType() {
        return pluginType;
    }

    public static PluginType getType() {
        String pluginType = getPluginTypeFromManifest();
        if (pluginType != null)
            return PluginType.valueOf(pluginType);

        throw new IllegalStateException("Unknown plugin type");
    }

    private static String getPluginTypeFromManifest() {
        try (JarFile jarFile = new JarFile(new File(PluginType.class.getProtectionDomain().getCodeSource().getLocation().toURI()))) {
            Manifest manifest = jarFile.getManifest();
            Attributes attributes = manifest.getMainAttributes();

            return attributes.getValue("Plugin-Type");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
