package me.whereareiam.socialismus.core.listener.state;

public interface State {
	static boolean isRequired() {
		return false;
	}

	static void setRequired(boolean state) {

	}
}
