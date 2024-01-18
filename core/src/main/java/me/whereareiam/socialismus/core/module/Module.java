package me.whereareiam.socialismus.core.module;

public interface Module {
	void initialize();

	boolean isEnabled();

	void reload();
}