package com.bluestacks.bugzy.utils;

/**
 * Created by msharma on 19/06/17.
 */

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;


@SharedPref(SharedPref.Scope.UNIQUE)
public interface PrefHelper {



    @DefaultBoolean(false)
    public boolean isUserLoggedIn();

    @DefaultString("")
    public String userEmail();

    @DefaultString("")
    public String userName();

    @DefaultString("")
    public String personId();

    @DefaultString("")
    public String accessToken();
}

