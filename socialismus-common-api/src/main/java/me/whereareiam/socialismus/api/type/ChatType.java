package me.whereareiam.socialismus.api.type;

/**
 * Represents different types of chat channels in the system.
 * Defines the scope and behavior of chat communication.
 */
public enum ChatType {
    /**
     * Represents a local chat channel with limited range.
     */
    LOCAL,

    /**
     * Represents a global chat channel visible to all players.
     */
    GLOBAL,

    /**
     * Represents a custom chat channel that would be implemented by other modules.
     */
    CUSTOM;

    /**
     * Checks if the chat type is LOCAL.
     *
     * @return true if the chat type is LOCAL, false otherwise
     */
    public boolean isLocal() {
        return this == LOCAL;
    }

    /**
     * Checks if the chat type is GLOBAL.
     *
     * @return true if the chat type is GLOBAL, false otherwise
     */
    public boolean isGlobal() {
        return this == GLOBAL;
    }
}