/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.io.Serializable;

/**
 *
 * @author Md. Abdulla Al Mamun
 * @time 5:08:05 PM
 * @date Oct 11, 2017
 */
public class User implements Serializable {

    private String userName;
    private final String userEmail;
    private String password;
    private final UserType userType;
    private Integer userID;

    public User(String name, String email, String password, UserType userType) {
        this.userName = name;
        this.userEmail = email;
        this.password = password;
        this.userType = userType;
        userID = null;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public UserType getUserType() {
        return userType;
    }

    public Integer getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return String.format("Name: %s\nE-mail: %s", userName, userEmail);
    }
}
