package com.bluestacks.bugzy.models.resp;


public class LoginRequest {
    protected final String cmd = "logon";
    protected String email;
    protected String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getCmd() {
        return cmd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
