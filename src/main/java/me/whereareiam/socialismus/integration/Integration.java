package me.whereareiam.socialismus.integration;

public interface Integration {
    void initialize();

    String getName();

    boolean isEnabled();
}
