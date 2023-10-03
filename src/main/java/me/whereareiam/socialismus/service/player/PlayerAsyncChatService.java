package me.whereareiam.socialismus.service.player;

public class PlayerAsyncChatService {
    private boolean chatListenerRequired = false;

    public boolean isChatListenerRequired() {
        return chatListenerRequired;
    }

    public void setChatListenerRequired(boolean required) {
        this.chatListenerRequired = required;
    }
}
