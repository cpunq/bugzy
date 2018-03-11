package com.bluestacks.bugzy.di.module;


import com.bluestacks.bugzy.ui.casedetails.CaseDetailsFragmentViewModel;
import com.bluestacks.bugzy.ui.home.HomeViewModel;
import com.bluestacks.bugzy.ui.home.MyCasesViewModel;
import com.bluestacks.bugzy.ui.home.PeopleViewModel;
import com.bluestacks.bugzy.ui.login.LoginViewModel;
import com.bluestacks.bugzy.ui.search.SearchActivityViewModel;
import com.bluestacks.bugzy.utils.BugzyViewModelFactory;
import com.bluestacks.bugzy.utils.ViewModelKey;

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
    abstract ViewModelProvider.Factory bindViewModelFactory(BugzyViewModelFactory factory);
}
