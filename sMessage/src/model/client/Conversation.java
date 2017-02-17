package model.client;

import java.util.ArrayList;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class Conversation {

    private final ClientUser talkingWithUser;
    private final ArrayList<Message> messages;

    public Conversation(ClientUser talkingWithUser) {
	this.talkingWithUser = talkingWithUser;
	messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
	messages.add(message);
    }

    public String getTalkingWithUsername() {
	return talkingWithUser.getUserName();
    }

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
