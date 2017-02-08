package model;

/**
 * @author s305046, s305080, s305084, s305089
 */
public enum Command {
    CONNECT,        // , userID
    REQUEST,        // , userID
    RESPONSE,       // , "yes"/"no", userID
    DISCONNECT,     // , userID
    GETUSERS,       // ,
    USERLIST,       //TODO
    REGUSER,        // , uname, encryptedPassWord
    LOGIN,          // , uname, encryptedPassWord
    LOGINFAIL,      // ,
    LOGINSUCCESS,   // ,
    ERROR           //
}
