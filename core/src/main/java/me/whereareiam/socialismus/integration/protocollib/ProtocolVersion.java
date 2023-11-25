package me.whereareiam.socialismus.integration.protocollib;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.version.Version;

import java.util.List;

@Singleton
public class ProtocolVersion {

    // Entity Meta
    private final List<Integer> metaVisibility = List.of(0);
    // AreaEffectCloud Meta
    private final List<Integer> metaRadius = List.of(8);
    // Display Meta
    private final List<Integer> metaScale = List.of(11, 12);
    private final List<Integer> metaDisplayType = List.of(14, 15);
    // TextDisplay Meta
    private final List<Integer> metaMessage = List.of(22, 23);
    private final List<Integer> metaLineWidth = List.of(23, 24);
    private final List<Integer> metaBackground = List.of(24, 25);
    private final List<Integer> metaOptions = List.of(26, 27);

    public Integer getMetaVisibility() {
        return getIndex(metaVisibility, Version.getVersion());
    }

    public int getMetaRadius() {
        return getIndex(metaRadius, Version.getVersion());
    }

    public Integer getMetaScale() {
        return getIndex(metaScale, Version.getVersion());
    }

    public Integer getMetaDisplayType() {
        return getIndex(metaDisplayType, Version.getVersion());
    }

    public Integer getMetaMessage() {
        return getIndex(metaMessage, Version.getVersion());
    }

    public Integer getMetaLineWidth() {
        return getIndex(metaLineWidth, Version.getVersion());
    }

    public Integer getMetaBackground() {
        return getIndex(metaBackground, Version.getVersion());
    }

    public Integer getMetaOptions() {
        return getIndex(metaOptions, Version.getVersion());
    }

    private Integer getIndex(List<Integer> list, Version version) {
        int index = version.ordinal();
        if (index >= list.size()) {
            return list.get(list.size() - 1);
        }
        return list.get(index);
    }
}

