package com.bluestacks.bugzy.di.module;

import com.bluestacks.bugzy.ui.casedetails.CaseDetailsActivity;
import com.bluestacks.bugzy.ui.casedetails.FullScreenImageActivity;
import com.bluestacks.bugzy.ui.editcase.CaseEditActivity;
import com.bluestacks.bugzy.ui.home.HomeActivity;
import com.bluestacks.bugzy.ui.login.LoginActivity;
import com.bluestacks.bugzy.ui.search.SearchActivity;
import com.bluestacks.bugzy.ui.splash.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector(modules = FragmentBindingModule.class)
    abstract HomeActivity contributeHomeActivity();

    @ContributesAndroidInjector(modules = FragmentBindingModule.class)
    abstract CaseDetailsActivity contributeCaseDetailsActivity();

    @ContributesAndroidInjector
    abstract FullScreenImageActivity contributeFullScreenImageActivity();

    @ContributesAndroidInjector
    abstract SearchActivity contributeSearchActivity();

    @ContributesAndroidInjector
    abstract CaseEditActivity contributeCaseEditActivity();

    @ContributesAndroidInjector(modules = FragmentBindingModule.class)
    abstract SplashActivity contributeSplashActivity();
}
