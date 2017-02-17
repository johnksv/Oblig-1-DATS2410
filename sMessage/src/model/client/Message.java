package model.client;

/**
 * @author s305089
 */
public class Message {

    private final String from;
    private final String message;

    public Message(String from, String message) {
	this.from = from;
	this.message = message;
    }

    public String getFrom() {
	return from;
    }

    public String getMessage() {
	return message;
    }

    @Override
    public String toString() {
	return from + ": " + message;
    }
}
