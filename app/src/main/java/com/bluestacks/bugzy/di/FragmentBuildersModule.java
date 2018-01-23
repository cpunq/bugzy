package com.bluestacks.bugzy.di;

import com.bluestacks.bugzy.ui.CaseDetailsFragment;
import com.bluestacks.bugzy.ui.FullScreenImageFragment;
import com.bluestacks.bugzy.ui.MyCasesFragment;
import com.bluestacks.bugzy.ui.PeopleFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract CaseDetailsFragment contributeRepoFragment();

    @ContributesAndroidInjector
    abstract FullScreenImageFragment contributeUserFragment();

    @ContributesAndroidInjector
    abstract MyCasesFragment contributeSearchFragment();

    @ContributesAndroidInjector
    abstract PeopleFragment contributePeopleFragment();
}
