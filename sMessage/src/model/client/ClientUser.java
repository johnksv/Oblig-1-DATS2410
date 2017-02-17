package model.client;

import model.Client;

/**
 * Created by trulsstenrud on 17/02/2017.
 */
public class ClientUser {
    private Status status;
    private final String userName;
    private ClientUser user;


   /* public ClientUser(String userName){
        this.userName = userName;
        status = Status.ONLINE;
    }*/
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

    public void setStatus(){

    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientUser && ((ClientUser) obj).getUserName().equals(getUserName());
    }

    public ClientUser getUser() {
        return user;
    }
}
