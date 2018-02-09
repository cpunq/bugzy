package com.bluestacks.bugzy.di.component;


import com.bluestacks.bugzy.BugzyApp;
import com.bluestacks.bugzy.di.module.ActivityBindingModule;
import com.bluestacks.bugzy.di.module.NetModule;
import com.bluestacks.bugzy.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AppModule.class, NetModule.class, AndroidSupportInjectionModule.class, ActivityBindingModule.class})
public interface NetComponent {
    void inject(BugzyApp app);
}
