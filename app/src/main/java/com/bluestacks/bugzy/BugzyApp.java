package com.bluestacks.bugzy;

import android.app.Activity;
import android.app.Application;

import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.di.AppInjector;
import com.bluestacks.bugzy.di.component.DaggerNetComponent;
import com.bluestacks.bugzy.di.module.AppModule;
import com.bluestacks.bugzy.di.module.NetModule;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class BugzyApp extends Application implements HasActivityInjector {
    @Inject DispatchingAndroidInjector<Activity> mActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
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

}
