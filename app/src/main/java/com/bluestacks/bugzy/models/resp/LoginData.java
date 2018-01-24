package com.bluestacks.bugzy.models.resp;

public class LoginData {
    protected String token;

    public LoginData(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
