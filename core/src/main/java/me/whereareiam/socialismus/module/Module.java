package me.whereareiam.socialismus.module;

public interface Module {
    boolean requiresChatListener();

    boolean requiresJoinListener();
}