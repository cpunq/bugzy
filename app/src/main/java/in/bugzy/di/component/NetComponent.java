package in.bugzy.di.component;


import in.bugzy.BugzyApp;
import in.bugzy.di.module.ActivityBindingModule;
import in.bugzy.di.module.NetModule;
import in.bugzy.di.module.AppModule;
import in.bugzy.di.module.ServiceBindingModule;
import in.bugzy.di.module.ViewModelModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AppModule.class, NetModule.class, AndroidSupportInjectionModule.class, ActivityBindingModule.class, ViewModelModule.class, ServiceBindingModule.class})
public interface NetComponent {
    void inject(BugzyApp app);
}
