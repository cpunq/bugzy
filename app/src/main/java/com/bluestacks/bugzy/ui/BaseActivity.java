package com.bluestacks.bugzy.ui;


import com.bluestacks.bugzy.data.local.PrefsHelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class BaseActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    @Inject
    protected PrefsHelper mPrefs;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    private String getAccessToken() {
        return mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN, "");
    }

    protected boolean isLoggedIn() {
        return !TextUtils.isEmpty(getAccessToken());
    }
}
