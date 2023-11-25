package me.whereareiam.socialismus.module;

public interface Module {
    void initialize();

    boolean isEnabled();

    void reload();
}