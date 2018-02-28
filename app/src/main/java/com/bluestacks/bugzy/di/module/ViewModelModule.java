package com.bluestacks.bugzy.di.module;


import com.bluestacks.bugzy.ui.login.LoginViewModel;
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
    abstract ViewModelProvider.Factory bindViewModelFactory(BugzyViewModelFactory factory);
}
