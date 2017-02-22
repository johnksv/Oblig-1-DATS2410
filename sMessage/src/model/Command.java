package model;

/**
 * List of legal commands in Simple Messaging Protocol (SMP).
 *
 * @author Member(1-2-3-4)
 */
public enum Command {
    /**
     * This command is only sent from client. Parameters: A new username, the
     * SHA256 encrypted password. This command is sent when a new user wants to
     * register.
     */
    REGUSER,
    /**
     * This command is only sent from server. Parameters: non. This command is
     * sent if something went wrong with registration.
     */
    REGUSERFAIL,
    /**
     * This command is only sent from client. Parameters: A new username, the
     * SHA256 encrypted password. This command is sent when a user wants to
     * login.
     */
    LOGIN,
    /**
     * This command is only sent from server. Parameters: the error message.
     * This command is sent if something went wrong with login. E.x. wrong
     * username, wrong password, etc.
     */
    LOGINFAIL,
    /**
     * This command is only sent from server. Parameters: non. This command is
     * sent if login was successful.
     */
    LOGINSUCCESS,
    /**
     * This command is only sent from client. Parameters: non. This command is
     * sent when the users log off.
     */
    LOGOFF,
    /**
     *
     * This command is only sent from client. Parameters: Username This command
     * is sent when a user wants to connect to another user. The server forwards
     * the message to the correct user, which get the option to accept or reject
     * the connection. A “CONNECT” is followed by a “RESPONSE”
     */
    CONNECT,
    /**
     * This command is only sent from client. Parameters: Username, response
     * This command is sent when a user response to the “CONNECT” request. The
     * username-parameter is the user that sent the “CONNECT” request. The
     * response-parameter is either “NO” or “YES”.
     */
    RESPONSE,
    /**
     * This command is only sent from client. Parameters: Username The
     * username-parameter is the user that you want to disconnect from. If the
     * two users want to start a new conversation, a “CONNECT” command must be
     * sent.
     */
    DISCONNECT,
    /**
     * This command is sent from client and server. From client: Parameters:
     * newStatus The newStatus-parameter is your new status. Can either be
     * Status.ONLINE or STATUS.BUSY
     *
     * From server: Parameters: username, newStatus The username-parameter is
     * the user that updates its status. The newStatus-parameter is your new
     * status. “+” for online, “-” for busy, “0” for offline. NB: This command
     * is sent to every user excluding “username”, when “username” updates its
     * status.
     */
    STATUSUPDATE,
    /**
     * This command is only sent from client. Parameters: non Request the server
     * to respond with a “USERLIST”
     */
    GETUSERS,
    /**
     * This command is only sent from server. Parameters: [Username, status]...
     * Sends back a list of username and the corresponding status, excluding
     * your username. The list may be empty if no other clients are connected to
     * the server.
     */
    USERLIST,
    /**
     * This command is only sent from server. Parameters: ErrorMessage Sends an
     * error message to the client if something went wrong with previous
     * command.
     */
    ERROR
}
