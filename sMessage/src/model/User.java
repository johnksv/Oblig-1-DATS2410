package model;

/**
 * A class dedicated to safly store passwords username and the status of users.
 * Status is online(True) and offline (false).
 * Busy is busy (true) and not busy (false) and can never be busy if not online.
 * Password is stored as a encrypted string so you cant get the original clear text password from this class.
 * 
 * @author Member(1-2-3-4)
 */
public class User {

    private final String uname, pswd;
    private boolean status;
    private boolean busy;
    
    /**
    * Initiates a new user, and stores the password and username.
    * Status is set here, but can be change later not like password and username.
    */
    public User(String uname, String pswd, boolean status) {
	this.uname = uname;
	this.pswd = pswd;
	this.status = status;
    }
    
    /**
     * Returns username
     * @return uname
     */
    public String getUname() {
	return uname;
    }
    
    /**
     * Sets status to false.
     * Sets busy to false.
     */
    public void logOff() {
	status = false;
        busy = false;
    }
    
    /**
     * Returns true if online.
     * @return status
     */
    public boolean isOnline() {
	return status;
    }
    
    /**
     * Sets busy true or false.
     * @param busy 
     */
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    /**
     * Returns true if busy.
     * @return busy
     */
    public boolean isBusy() {
	return busy;
    }

    public String getStatus(){
        return status ? (busy ? "Busy" : "Online") : "Offline";
    }
    
    /**
     * Sets status as true.
     * Will throw exception if already logged in or if pswd does not equals the stored password. 
     * @param pswd Password
     * @throws LoginException 
     */
    public void login(String pswd) throws LoginException {

        if (status) {
            throw new LoginException("Allready logged in!");
	}
	if (!pswd.equals(this.pswd)) {
	    throw new LoginException("Wrong password!");
	}
	status = true;
    }

    @Override
    public String toString() {

	return "User{" + "uname=" + uname + ", status=" + status + '}';
    }

}
