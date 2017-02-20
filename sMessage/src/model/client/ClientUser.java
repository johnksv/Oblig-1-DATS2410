package model.client;

import java.util.Objects;


/**
 * @author s305046, s305080, s305084, s305089
 */
public class ClientUser {
    private Status status;
    private final String userName;


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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

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
