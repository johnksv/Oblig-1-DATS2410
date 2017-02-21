package model.client;

import java.util.ArrayList;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class Conversation {

    private final ClientUser talkingWithUser;
    private final ArrayList<Message> messages;

    /**
     * Constructor for the Conversation class.
     * @param talkingWithUser The conversation is between the owner of the object and talkingWithUser.
     */
    public Conversation(ClientUser talkingWithUser) {
	this.talkingWithUser = talkingWithUser;
	messages = new ArrayList<>();
    }

    /**
     * Adds a message to the conversation.
     * @param message The message to be added.
     */
    public void addMessage(Message message) {
	messages.add(message);
    }

    /**
     * A getter for the talkingWithUsername variable.
     * @return THe string talkingWithUsername.
     */
    public String getTalkingWithUsername() {
	return talkingWithUser.getUserName();
    }

    /**
     * A getter for the
     * @return
     */
    public ClientUser getClientUser(){
        return talkingWithUser;
    }

    public Status getTalkingWidthStatus(){return talkingWithUser.getStatus();}

    public ArrayList<Message> getMessages() {
	return messages;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Conversation &&
                ((Conversation) obj).getTalkingWithUsername().equals(getTalkingWithUsername());
    }

}
