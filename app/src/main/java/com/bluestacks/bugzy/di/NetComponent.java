package com.bluestacks.bugzy.di;


import com.bluestacks.bugzy.BugzyApp;
import com.bluestacks.bugzy.MainActivity;
import com.bluestacks.bugzy.MainActivity_;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AppModule.class, NetModule.class, AndroidSupportInjectionModule.class, MainActivityModule.class})
public interface NetComponent {
    void inject(BugzyApp app);
}
