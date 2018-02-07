package com.bluestacks.bugzy.di;

import com.bluestacks.bugzy.ui.CaseDetailsActivity;
import com.bluestacks.bugzy.ui.FullScreenImageActivity;
import com.bluestacks.bugzy.ui.HomeActivity;
import com.bluestacks.bugzy.ui.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract HomeActivity contributeHomeActivity();

    @ContributesAndroidInjector
    abstract CaseDetailsActivity contributeCaseDetailsActivity();

    @ContributesAndroidInjector
    abstract FullScreenImageActivity contributeFullScreenImageActivity();
}
