package me.whereareiam.socialismus.core.integration;

public interface Integration {
	void initialize();

	String getName();

	boolean isEnabled();

	IntegrationType getType();
}
