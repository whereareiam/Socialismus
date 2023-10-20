package me.whereareiam.socialismus.config.feature.bubblechat;

import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.type.AlignmentType;
import me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.type.DisplayType;
import org.joml.Vector3f;

public class BubbleChatSettingsConfig {
    public AlignmentType alignmentType = AlignmentType.CENTER;
    public DisplayType displayType = DisplayType.VERTICAL;
    public boolean seeThrough = false;
    public int lineWidth = 100;
    public int lineCount = 5;
    public int headDistance = 1;
    public double timePerSymbol = 0.1;
    public double minimumTime = 1.5;
    public Vector3f displayScale = new Vector3f((float) 1.0, (float) 1.0, (float) 1.0);
}
