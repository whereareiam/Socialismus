package me.whereareiam.socialismus.api.exception;

public class ModuleLifecycleException extends RuntimeException {
	public ModuleLifecycleException(String message) {
		super(message);
	}

	public ModuleLifecycleException(String message, Throwable cause) {
		super(message, cause);
	}
}