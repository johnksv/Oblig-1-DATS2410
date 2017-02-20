package model;

/**
 * List of legal commands in Simple Messaging Protocol SMP.
 * For information regarding each command read the "readme" file.
 * @author s305046, s305080, s305084, s305089
 */
public enum Command {
    CONNECT,        // , userID
    RESPONSE,       // , "yes"/"no", userID
    DISCONNECT,     // , userID
    STATUSUPDATE,   // , userID, +/-/0
    GETUSERS,       // ,
    USERLIST,       //  TODO
    REGUSER,        // , uname, encryptedPassWord
    LOGIN,          // , uname, encryptedPassWord
    LOGINFAIL,      // ,
    LOGINSUCCESS,   // ,
    LOGOFF,         //
    REGUSERFAIL, ERROR           //
}
