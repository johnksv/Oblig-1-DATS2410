package model;

/**
 * Signals that an "Login exception" of some sort has occurred. 
 * This class is for general exceptions produced by failed attempts to login to server.
 * 
 * @author s305046, s305080, s305084, s305089
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
