package in.bugzy.di.module;


import in.bugzy.ui.about.AboutActivityViewModel;
import in.bugzy.ui.about.AboutFragmentViewModel;
import in.bugzy.ui.casedetails.CaseDetailsFragmentViewModel;
import in.bugzy.ui.editcase.CaseEditViewModel;
import in.bugzy.ui.home.HomeViewModel;
import in.bugzy.ui.home.MyCasesViewModel;
import in.bugzy.ui.home.PeopleViewModel;
import in.bugzy.ui.login.LoginViewModel;
import in.bugzy.ui.search.SearchActivityViewModel;
import in.bugzy.ui.splash.SplashViewModel;
import in.bugzy.utils.BugzyViewModelFactory;
import in.bugzy.utils.ViewModelKey;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel homeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MyCasesViewModel.class)
    abstract ViewModel bindMyCasesViewModel(MyCasesViewModel myCasesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CaseDetailsFragmentViewModel.class)
    abstract ViewModel bindCaseDetailsFragmentViewModel(CaseDetailsFragmentViewModel caseDetailsFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PeopleViewModel.class)
    abstract ViewModel bindPeopleViewModel(PeopleViewModel peopleViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchActivityViewModel.class)
    abstract ViewModel bindSearchActivityViewModel(SearchActivityViewModel searchActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CaseEditViewModel.class)
    abstract ViewModel bindCaseEditViewModel(CaseEditViewModel caseEditViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel.class)
    abstract ViewModel bindSplashViewModel(SplashViewModel splashViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AboutFragmentViewModel.class)
    abstract ViewModel bindAboutFragmentViewModel(AboutFragmentViewModel aboutFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AboutActivityViewModel.class)
    abstract ViewModel bindAboutActivityViewModel(AboutActivityViewModel aboutActivityViewModel);


    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(BugzyViewModelFactory factory);
}
