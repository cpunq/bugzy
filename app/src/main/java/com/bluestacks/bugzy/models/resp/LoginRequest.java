package com.bluestacks.bugzy.models.resp;


import com.bluestacks.bugzy.models.Request;

public class LoginRequest extends Request {
    protected String email;
    protected String password;

    public LoginRequest(String email, String password) {
        super("logon");
        this.email = email;
        this.password = password;
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
