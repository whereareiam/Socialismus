package me.whereareiam.socialismus.integration.protocollib.entity.metadata.display;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.type.AlignmentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class TextDisplayMetadata extends DisplayMetadata {
    private Component message;
    private int lineWidth = -1;
    private int background = -1;
    private boolean hasShadow;
    private boolean canSeeThrough;
    private AlignmentType alignmentType;

    public void setMessage(Component message) {
        this.message = message;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setBackground(String color, int opacity) {
        int r = Integer.parseInt(color.substring(1, 3), 16);
        int g = Integer.parseInt(color.substring(3, 5), 16);
        int b = Integer.parseInt(color.substring(5, 7), 16);

        this.background = (opacity << 24) | (r << 16) | (g << 8) | b;
    }

    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
    }

    public void setCanSeeThrough(boolean canSeeThrough) {
        this.canSeeThrough = canSeeThrough;
    }

    public void setAlignmentType(AlignmentType alignmentType) {
        this.alignmentType = alignmentType;
    }

    @Override
    public WrappedDataWatcher getMetadata() {
        super.getMetadata();

        if (message != null) {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    23,
                    WrappedDataWatcher.Registry.getChatComponentSerializer(false)
            ), WrappedChatComponent.fromJson(
                    GsonComponentSerializer.gson().serialize(message)
            ).getHandle());
        }

        if (lineWidth != -1) {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    24,
                    WrappedDataWatcher.Registry.get(Integer.class)
            ), lineWidth);
        }

        if (background != -1) {
            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    25,
                    WrappedDataWatcher.Registry.get(Integer.class)
            ), background);
        }

        if (hasShadow | canSeeThrough | alignmentType != null) {
            byte properties = 0x00;
            if (hasShadow) {
                properties |= 0x01;
            }

            if (canSeeThrough) {
                properties |= 0x02;
            }

            if (alignmentType != null) {
                properties |= alignmentType.getValue();
            }

            metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(
                    27,
                    WrappedDataWatcher.Registry.get(Byte.class)
            ), properties);
        }

        return metadata;
    }
}
