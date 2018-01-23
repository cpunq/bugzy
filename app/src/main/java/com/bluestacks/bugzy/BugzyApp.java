package com.bluestacks.bugzy;

import android.app.Activity;
import android.app.Application;

import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.di.AppInjector;
import com.bluestacks.bugzy.di.AppModule;
import com.bluestacks.bugzy.di.DaggerNetComponent;
import com.bluestacks.bugzy.di.NetComponent;
import com.bluestacks.bugzy.di.NetModule;
import com.bluestacks.bugzy.models.resp.Person;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BugzyApp extends Application implements HasActivityInjector {
    private NetComponent mNetComponent;
    public List<Person> persons;

    @Inject DispatchingAndroidInjector<Activity> mActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

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
