package com.bluestacks.bugzy.di;

import com.bluestacks.bugzy.ui.casedetails.CaseDetailsFragment;
import com.bluestacks.bugzy.ui.home.MyCasesFragment;
import com.bluestacks.bugzy.ui.home.PeopleFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract MyCasesFragment contributeSearchFragment();

    @ContributesAndroidInjector
    abstract PeopleFragment contributePeopleFragment();

    @ContributesAndroidInjector
    abstract CaseDetailsFragment contributeCaseDetailsFragment();
}
