package me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.type;

public enum AlignmentType {
    CENTER(0x00),
    LEFT(0x08),
    RIGHT(0x10);

    private final int value;

    AlignmentType(int value) {
        this.value = value;
    }

    public byte getValue() {
        return (byte) value;
    }
}
