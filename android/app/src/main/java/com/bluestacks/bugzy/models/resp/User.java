package com.bluestacks.bugzy.models.resp;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by msharma on 06/06/17.
 */
@Root(name = "response")
public class User {

    @Element(name = "token")
    private String mAuthToken;

    public String getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(String authToken) {
        this.mAuthToken = authToken;
    }

}
