package me.whereareiam.socialismus.integration.protocollib.entity.metadata.display.type;

public enum DisplayType {
    FIXED((byte) 0x00),
    VERTICAL((byte) 0x01),
    HORIZONTAL((byte) 0x02),
    CENTER((byte) 0x03);

    private final byte value;

    DisplayType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
