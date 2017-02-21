package model.client;

import java.util.ArrayList;

/**
 * @author Member(1-2-3-4)
 */
public class Conversation {

    private final ClientUser talkingWithUser;
    private final ArrayList<Message> messages;

    /**
     * Constructor for the Conversation class.
     * @param talkingWithUser The conversation is between the owner of the object and the clientUser talkingWithUser.
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
     * A getter for the username of the conversation partner.
     * @return THe string talkingWithUser.
     */
    public String getTalkingWithUsername() {
	return talkingWithUser.getUserName();
    }

    /**
     * A getter for the clientUser talkingWithUser.
     * @return The clientUser you are talking to.
     */
    public ClientUser getClientUser(){
        return talkingWithUser;
    }

    /**
     * Getter for all the messages.
     * @return all the messages in an array.
     */
    public ArrayList<Message> getMessages() {
	return messages;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Conversation &&
                ((Conversation) obj).getTalkingWithUsername().equals(getTalkingWithUsername());
    }

}
