package model.client;

import model.Status;
import java.util.Objects;


/**
 * @author Member(1-2-3-4)
 */
public class ClientUser {
    private Status status;
    private final String userName;

    /**
     * Constructor for the ClienUser class.
     * @param userName The username of this ClientUser.
     * @param status The status of this clientuser represented by +, - or 0.
     */
    public ClientUser(String userName, String status){
        this.userName = userName;
        switch(status){
            case "+":
                this.status = Status.ONLINE;
                break;
            case "-":
                this.status = Status.BUSY;
                break;
            case "0":
                this.status = Status.OFFLINE;
                break;

        }
    }

    /**
     * A getter for the status.
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * A setter for the status.
     * @param status The status to be sett.
     */
    public void setStatus(Status status){
        this.status = status;
    }

    /**
     * Getter for the username.
     * @return
     */
    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientUser && ((ClientUser) obj).getUserName().equals(getUserName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.userName);
        return hash;
    }
}
