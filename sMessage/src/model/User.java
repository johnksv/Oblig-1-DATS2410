package model;

/**
 * A class dedicated to safely store passwords username and the status of users.
 * Password is stored as a encrypted SHA256 string so you cant get the original
 * clear text password from this class. This is to ensure the users privacy
 *
 * @author Member(1-2-3-4)
 */
public class User {

    private final String uname, pswd;
    private Status status;

    /**
     * Initiates a new user, and stores the password and username. Status is set
     * here, but can be change later not like password and username.
     * @see Status
     * @param uname Username
     * @param pswd Password
     * @param status 
     */
    public User(String uname, String pswd, Status status) {
	this.uname = uname;
	this.pswd = pswd;
	this.status = status;
    }

    /**
     * Returns username
     *
     * @return The username
     */
    public String getUname() {
	return uname;
    }

    /**
     * Sets the user offline
     */
    public void logOff() {
	status = Status.OFFLINE;
    }

    /**
     * Returns true if online.
     *
     * @return If the user has status online
     */
    public boolean isOnline() {
	return status == Status.ONLINE;
    }

    /**
     * Returns true if busy.
     *
     * @return If the user has status busy
     */
    public boolean isBusy() {
	return status == Status.BUSY;
    }

    /**
     * Sets this status to the given value
     *
     * @param status the new status of the user
     */
    public void setStatus(Status status) {
	this.status = status;
    }

    /**
     * Returns the users status
     *
     * @return status
     */
    public Status getStatus() {
	return status;
    }

    /**
     * Sets status as true. Will throw exception if already logged in or if pswd
     * does not equals the stored password.
     *
     * @param pswd the SHA256 hash of the password
     * @throws LoginException Thrown is user are already logged in or uses wrong
     * password
     */
    public void login(String pswd) throws LoginException {

	if (status == Status.ONLINE) {
	    throw new LoginException("Already logged in!");
	}
	if (!pswd.equals(this.pswd)) {
	    throw new LoginException("Wrong password!");
	}
	status = Status.ONLINE;
    }

    @Override
    public String toString() {

	return uname + ";" + pswd;
    }

}
