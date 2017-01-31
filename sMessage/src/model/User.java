package model;

/**
 * @author s305046, s305080, s305084, s305089
 */
public class User {

    private final String uname, pswd;
    private boolean status;

    public User(String uname, String pswd) {
	this.uname = uname;
	this.pswd = pswd;
    }

    public String getUname() {
	return uname;
    }

    public void logOff() {
	status = false;
    }

    public boolean isOnline() {
	return status;
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
