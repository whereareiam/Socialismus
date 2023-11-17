package me.whereareiam.socialismus.module;

public interface IModule {
    boolean requiresChatListener();

    boolean requiresJoinListener();
}