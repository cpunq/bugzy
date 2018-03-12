package com.bluestacks.bugzy.data.remote.model;

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
