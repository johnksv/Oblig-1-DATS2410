package model.client;

import java.util.ArrayList;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class Conversation {

    private final String talkingWithUsername;
    private final ArrayList<Message> messages;

    public Conversation(String talkingWithUsername) {
	this.talkingWithUsername = talkingWithUsername;
	messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
	messages.add(message);
    }

    public String getTalkingWithUsername() {
	return talkingWithUsername;
    }

    public ArrayList<Message> getMessages() {
	return messages;
    }

}
