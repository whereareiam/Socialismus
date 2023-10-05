package me.whereareiam.socialismus.service;

public class ChatService {
    private boolean chatListenerRequired = false;

    public boolean isChatListenerRequired() {
        return chatListenerRequired;
    }

    public void setChatListenerRequired(boolean required) {
        this.chatListenerRequired = required;
    }
}
