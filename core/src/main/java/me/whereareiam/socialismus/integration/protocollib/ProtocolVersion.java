package me.whereareiam.socialismus.integration.protocollib;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.version.Version;

import java.util.List;

@Singleton
public class ProtocolVersion {
    private final Version version;

    // Entity Meta
    private final List<Integer> metaVisibility = List.of(0);
    // Mob Meta
    private final List<Integer> metaHasAI = List.of(15);
    // Display Meta
    private final List<Integer> metaScale = List.of(11, 12);
    private final List<Integer> metaDisplayType = List.of(14, 15);
    // TextDisplay Meta
    private final List<Integer> metaMessage = List.of(22, 23);
    private final List<Integer> metaLineWidth = List.of(23, 24);
    private final List<Integer> metaBackground = List.of(24, 25);
    private final List<Integer> metaOptions = List.of(26, 27);

    @Inject
    public ProtocolVersion(Version version) {
        this.version = version;
    }

    public Integer getMetaVisibility() {
        return getIndex(metaVisibility, version.getVersion());
    }

    public Integer getMetaHasAI() {
        return getIndex(metaHasAI, version.getVersion());
    }

    public Integer getMetaScale() {
        return getIndex(metaScale, version.getVersion());
    }

    public Integer getMetaDisplayType() {
        return getIndex(metaDisplayType, version.getVersion());
    }

    public Integer getMetaMessage() {
        return getIndex(metaMessage, version.getVersion());
    }

    public Integer getMetaLineWidth() {
        return getIndex(metaLineWidth, version.getVersion());
    }

    public Integer getMetaBackground() {
        return getIndex(metaBackground, version.getVersion());
    }

    public Integer getMetaOptions() {
        return getIndex(metaOptions, version.getVersion());
    }

    private Integer getIndex(List<Integer> list, Version version) {
        int index = version.ordinal();
        if (index >= list.size()) {
            return list.get(list.size() - 1);
        }
        return list.get(index);
    }
}

