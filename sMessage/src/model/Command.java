package model;

/**
 * List of legal commands in Simple Messaging Protocol SMP. For information
 * regarding each command read the "readme" file.
 *
 * @author s305046, s305080, s305084, s305089
 */
public enum Command {
    /**
     *
     */
    CONNECT,
    /**
     *
     */
    RESPONSE,
    /**
     *
     */
    DISCONNECT,
    /**
     *
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
