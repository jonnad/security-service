package com.ventyx.security.admin;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Supports login functionality for the application
 *
    public static final String ERROR = "error"
    public static final String INPUT = "input"
    public static final String LOGIN = "login"
    public static final String NONE = "none"
    public static final String SUCCESS = "success"

 */
public class LoginAction extends ActionSupport {

    @Override
    public void validate() {
        if (isInvalid(getUsername())) addFieldError("username", "Username is required");

        if (isInvalid(getPassword())) addFieldError("password", "Password is required");
    }

    @Override
    public String execute() throws Exception {
        return SUCCESS;
    }

    private boolean isInvalid(String value) {
        return (value == null || value.length() == 0);
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
