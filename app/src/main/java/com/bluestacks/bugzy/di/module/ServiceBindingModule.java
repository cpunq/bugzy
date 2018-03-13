package com.bluestacks.bugzy.di.module;

import com.bluestacks.bugzy.utils.BugzyDataSyncService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBindingModule {
    @ContributesAndroidInjector
    abstract BugzyDataSyncService contributeSyncService();
}
