package model;

/**
 * Signals that an "Login exception" of some sort has occurred. 
 * This class is for general exceptions produced by failed attempts to login to server.
 * 
 * @author Member(1-2-3-4)
 */
public class LoginException extends Exception {

    /**
     * Error constructor.
     * @param msg error message.
     */
    public LoginException(String msg) {
	super(msg);

    }

}
