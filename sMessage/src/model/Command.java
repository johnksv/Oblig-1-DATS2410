package model;

/**
 * List of legal commands in Simple Messaging Protocol SMP. For information
 * regarding each command read the "readme" file.
 *
 * @author s305046, s305080, s305084, s305089
 */
public enum Command {
    /**
     * When this command is sent to server, it is followed by the name of the destination user.
     * When this command is sent to a client it is followed by the name of the sender.
     */
    CONNECT,
    /**
     *  When this command is sent to server, it is followed by destination user and yes/no
     *  When this command is sent to a client, it is followed by source user and ues/no.
     */
    RESPONSE,
    /**
     * When this command is sent to server, it is followed by destination user.
     * When this command is sent to a client, it is followed by source user.
     */
    DISCONNECT,
    /**
     * When this command is sent to server, it is followed by +/-/0 representing the status of the user of the
     * associated SocketInstance.
     * When this command is sent to a client, it is followed by a username, and the status of that user.
     */
    STATUSUPDATE,
    /**
     *  Is used to ask server for a list of all the users.
     */
    GETUSERS,
    /**
     * Is followed by all the users and their status (+/-/0)
     */
    USERLIST,
    /**
     * When this command is sent to server, it is followed by a new username and the (Secure Hash Algorithm)SHA256 encrypted password.
     */
    REGUSER,
    /**
     * When this command is sent to server, it is followed by username and the (Secure Hash Algorithm)SHA256 encrypted password.
     */
    LOGIN,
    /**
     * Is used to tell the client that the login failed, and it is followed by an error message.
     */
    LOGINFAIL,
    /**
     * Is used to tell the client that the login succeeded.
     */
    LOGINSUCCESS,
    /**
     * Is used to tell the server that te user of this SocketInstance is logging off.
     */
    LOGOFF,
    /**
     * Is used to tell the client that the user registration failed, and it is followed by an error message
     */
    REGUSERFAIL,
    /**
     * Is used to tell the client that something went wrong, and it is followed by an error message.
     */
    ERROR
}
