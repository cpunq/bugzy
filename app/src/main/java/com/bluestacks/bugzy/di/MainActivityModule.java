package com.bluestacks.bugzy.di;

import com.bluestacks.bugzy.HomeActivity;
import com.bluestacks.bugzy.ui.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract HomeActivity contributeHomeActivity();
}
