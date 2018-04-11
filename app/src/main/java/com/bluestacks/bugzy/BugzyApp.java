package com.bluestacks.bugzy;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.text.TextUtils;

import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.data.remote.HostSelectionInterceptor;
import com.bluestacks.bugzy.di.AppInjector;
import com.bluestacks.bugzy.di.component.DaggerNetComponent;
import com.bluestacks.bugzy.di.module.AppModule;
import com.bluestacks.bugzy.di.module.NetModule;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.Random;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;

public class BugzyApp extends Application implements HasActivityInjector, HasServiceInjector {
    public static final String TAG = BugzyApp.class.getName();
    /**
     * Can't have this in a Repository, because the Activities need it
     * even before the setContentView()
     */
    private int mAppliedTheme = Const.LIGHT_THEME;

    @Inject DispatchingAndroidInjector<Activity> mActivityInjector;

    @Inject DispatchingAndroidInjector<Service> mServiceDispatchingAndroidInjector;

    @Inject
    HostSelectionInterceptor mHostSelectionInterceptor;

    @Inject
    PrefsHelper mPrefsHelper;

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

        // Setting the host after Dagger initialisation
        // Beware: diskIO on mainthread ;)
        String org = mPrefsHelper.getString(PrefsHelper.Key.ORGANISATION);
        if (!TextUtils.isEmpty(org)) {
            mHostSelectionInterceptor.setHost(org+".manuscript.com");
        }

        // Initialise Fabric
        Fabric.with(this, new Crashlytics());
        // log user in Crashlytics after DI
        setCrashlyticsUserIfPresent();
        Crashlytics.setString("organisation", org);

        // Set the applied theme
        int theme = mPrefsHelper.getInt(PrefsHelper.Key.THEME, -1);
        if (theme != -1) {
            mAppliedTheme = theme;
        } else {
            // Randomly generate a theme
            mAppliedTheme = new Random().nextBoolean() ? Const.DARK_THEME : Const.LIGHT_THEME;
            mPrefsHelper.setInt(PrefsHelper.Key.THEME, mAppliedTheme);
        }
    }

    private void setCrashlyticsUserIfPresent() {
        if (TextUtils.isEmpty(mPrefsHelper.getString(PrefsHelper.Key.USER_EMAIL))) {
            return;
        }
        Person me = new Person();
        me.setFullname(mPrefsHelper.getString(PrefsHelper.Key.USER_NAME));
        me.setPersonid(mPrefsHelper.getInt(PrefsHelper.Key.PERSON_ID));
        me.setEmail(mPrefsHelper.getString(PrefsHelper.Key.USER_EMAIL));

        Crashlytics.setUserIdentifier(me.getPersonid()+ "");
        Crashlytics.setUserEmail(me.getEmail());
        Crashlytics.setUserName(me.getFullname());
    }

    public int getAppliedTheme() {
        return mAppliedTheme;
    }

    /**
     * Will be called by repository, when theme is changed
     * @param appliedTheme
     */
    public void applyTheme(int appliedTheme) {
        mAppliedTheme = appliedTheme;
        mPrefsHelper.setInt(PrefsHelper.Key.THEME, mAppliedTheme);
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
