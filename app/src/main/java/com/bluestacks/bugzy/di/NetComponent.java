package com.bluestacks.bugzy.di;


import com.bluestacks.bugzy.BugzyApp;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AppModule.class, NetModule.class, AndroidSupportInjectionModule.class, MainActivityModule.class})
public interface NetComponent {
    void inject(BugzyApp app);
}
