package in.bugzy.di.module;

import in.bugzy.utils.BugzyDataSyncService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBindingModule {
    @ContributesAndroidInjector
    abstract BugzyDataSyncService contributeSyncService();
}
