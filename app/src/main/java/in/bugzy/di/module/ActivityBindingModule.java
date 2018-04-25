package in.bugzy.di.module;

import in.bugzy.ui.about.AboutActivity;
import in.bugzy.ui.casedetails.CaseDetailsActivity;
import in.bugzy.ui.casedetails.FullScreenImageActivity;
import in.bugzy.ui.editcase.CaseEditActivity;
import in.bugzy.ui.home.HomeActivity;
import in.bugzy.ui.login.LoginActivity;
import in.bugzy.ui.search.SearchActivity;
import in.bugzy.ui.splash.SplashActivity;

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

    @ContributesAndroidInjector(modules = FragmentBindingModule.class)
    abstract AboutActivity contributAboutActivity();
}
