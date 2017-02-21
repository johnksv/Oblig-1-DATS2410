package model;

/**
 * List of legal commands in Simple Messaging Protocol SMP. For information
 * regarding each command read the "readme" file.
 *
 * @author s305046, s305080, s305084, s305089
 */
public enum Command {
    /**
     * When this command is sent to server, it is followed by the name of the destination user
     * When this command is sent to a client it is followed by the name of the sender
     */
    CONNECT,
    /**
     *  When this command is sent to server, it is followed by destination user and yes/no
     *  When this command is sent to a client, it is followed by source user and ues/no
     */
    RESPONSE,
    /**
     * When this command is sent to server, it is followed by destination user
     * When this command is sent to a client, it is followed by source user.
     */
    DISCONNECT,
    /**
     * When this command is sent to server, it is followed by +/-/0 representing the status of the user of the
     * associated SocketInstance
     * When this command is sent to a client, it is followed by a username, and the status of that user
     */
    STATUSUPDATE,
    /**
     *
     */
    GETUSERS,
    /**
     *
     */
    USERLIST,
    /**
     *
     */
    REGUSER,
    /**
     *
     */
    LOGIN,
    /**
     *
     */
    LOGINFAIL,
    /**
     *
     */
    LOGINSUCCESS,
    /**
     *
     */
    LOGOFF,
    /**
     *
     */
    REGUSERFAIL,
    /**
     *
     */
    ERROR

}
