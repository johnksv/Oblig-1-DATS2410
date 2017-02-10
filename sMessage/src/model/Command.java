package model;

/**
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
    ERROR           //
}
