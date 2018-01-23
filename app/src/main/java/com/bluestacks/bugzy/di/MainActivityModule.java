package com.bluestacks.bugzy.di;

import com.bluestacks.bugzy.HomeActivity;
import com.bluestacks.bugzy.MainActivity_;
import com.bluestacks.bugzy.PeopleActivity_;
import com.bluestacks.bugzy.ui.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract MainActivity_ contributeMainActivity();

    @ContributesAndroidInjector
    abstract PeopleActivity_ contributePeopleActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract HomeActivity contributeHomeActivity();
}
