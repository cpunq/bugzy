package com.bluestacks.bugzy.di;

import com.bluestacks.bugzy.ui.CaseDetailsFragment_;
import com.bluestacks.bugzy.ui.FullScreenImageFragment_;
import com.bluestacks.bugzy.ui.MyCasesFragment;
import com.bluestacks.bugzy.ui.PeopleFragment_;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract CaseDetailsFragment_ contributeRepoFragment();

    @ContributesAndroidInjector
    abstract FullScreenImageFragment_ contributeUserFragment();

    @ContributesAndroidInjector
    abstract MyCasesFragment contributeSearchFragment();

    @ContributesAndroidInjector
    abstract PeopleFragment_ contributePeopleFragment();
}
