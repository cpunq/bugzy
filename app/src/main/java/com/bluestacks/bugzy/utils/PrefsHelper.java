package com.bluestacks.bugzy.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefsHelper {
    private final String SHARED_PREFS_NAME = "bugzy_prefs";
    private final SharedPreferences mPrivatePrefs;

    public enum Key {
        USER_LOGGED_IN,
        USER_EMAIL,
        USER_NAME,
        PERSON_ID,
        ACCESS_TOKEN
    }

    public PrefsHelper(Context context) {
        mPrivatePrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean getBoolean(Key key, boolean defaultVal) {
        return mPrivatePrefs.getBoolean(key.name(), defaultVal);
    }

    public void setBoolean(Key key, Boolean value) {
        mPrivatePrefs.edit().putBoolean(key.name(), value).apply();
    }

    public String getString(Key key, String defaultVal) {
        return mPrivatePrefs.getString(key.name(), defaultVal);
    }

    public String getString(Key key) {
        return this.getString(key, "");
    }

    public void setString(Key key, String value) {
        mPrivatePrefs.edit().putString(key.name(), value).commit();
    }

}

