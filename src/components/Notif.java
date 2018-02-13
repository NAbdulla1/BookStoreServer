package components;

import java.io.Serializable;

/**
 * Created by nayon on 13-Feb-18.
 */

public class Notif implements Serializable {
    private int notifID;
    private int customerID;
    private int publisherID;
    private UserType userType;
    private String message;
    private boolean isNew;

    Notif(){

    }

    public Notif(int notifID, int customerID, int publisherID, UserType userType, String message, boolean isNew) {
        this.notifID = notifID;
        this.customerID = customerID;
        this.publisherID = publisherID;
        this.userType = userType;
        this.message = message;
        this.isNew = isNew;
    }

    public int getNotifID() {
        return notifID;
    }

    public void setNotifID(int notifID) {
        this.notifID = notifID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(int publisherID) {
        this.publisherID = publisherID;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
