package com.ventyx.security.api.model;

/**
 * Created with IntelliJ IDEA.
 * User: USJOHAM
 * Date: 12/12/12
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class User {

    private int id;
    private String userId;
    private String firstName;
    private String lastName;
    private String credential;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
