package model;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class User {

    private final String uname, pswd;
    private boolean status;
    private boolean busy;

    public User(String uname, String pswd, boolean status) {
	this.uname = uname;
	this.pswd = pswd;
	this.status = status;
    }

    public String getUname() {
	return uname;
    }

    public void logOff() {
	status = false;
        busy = false;
    }

    public boolean isOnline() {
	return status;
    }
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean isBusy() {
	return busy;
    }

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
