package com.bluestacks.bugzy;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.util.Log;

import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.di.AppInjector;
import com.bluestacks.bugzy.di.component.DaggerNetComponent;
import com.bluestacks.bugzy.di.module.AppModule;
import com.bluestacks.bugzy.di.module.NetModule;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;

public class BugzyApp extends Application implements HasActivityInjector, HasServiceInjector {
    public static final String TAG = BugzyApp.class.getName();

    @Inject DispatchingAndroidInjector<Activity> mActivityInjector;

    @Inject DispatchingAndroidInjector<Service> mServiceDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Bugzy App onCreate()");
        DaggerNetComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .netModule(new NetModule(Const.API_BASE_URL))
                .build()
                .inject(this);
        AppInjector.init(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return mActivityInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return mServiceDispatchingAndroidInjector;
    }
}
