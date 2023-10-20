package me.whereareiam.socialismus.feature;

public interface Feature {
    boolean requiresChatListener();

    boolean requiresJoinListener();
}