package model.client;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class Message {

    private final String from;
    private final String message;

    /**
     * Constructor for a Message object
     * @param from The sender of this message.
     * @param message The content of this message.
     */
    public Message(String from, String message) {
	this.from = from;
	this.message = message;
    }

    /**
     * A getter for the sender of this message.
     * @return The sender of this message.
     */
    public String getFrom() {
	return from;
    }

    /**
     * A getter for the content of this message.
     * @return The content of this message.
     */
    public String getMessage() {
	return message;
    }

    @Override
    public String toString() {
	return from + ": " + message;
    }
}
