package com.bluestacks.bugzy.di.module;

import com.bluestacks.bugzy.ui.casedetails.CaseDetailsActivity;
import com.bluestacks.bugzy.ui.casedetails.FullScreenImageActivity;
import com.bluestacks.bugzy.ui.home.HomeActivity;
import com.bluestacks.bugzy.ui.login.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract HomeActivity contributeHomeActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract CaseDetailsActivity contributeCaseDetailsActivity();

    @ContributesAndroidInjector
    abstract FullScreenImageActivity contributeFullScreenImageActivity();
}
