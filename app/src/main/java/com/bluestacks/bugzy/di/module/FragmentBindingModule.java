package com.bluestacks.bugzy.di.module;

import com.bluestacks.bugzy.ui.about.AboutActivityFragment;
import com.bluestacks.bugzy.ui.casedetails.CaseDetailsFragment;
import com.bluestacks.bugzy.ui.home.MyCasesFragment;
import com.bluestacks.bugzy.ui.home.PeopleFragment;
import com.bluestacks.bugzy.ui.login.CredentialsFragment;
import com.bluestacks.bugzy.ui.login.KnowledgeFragment;
import com.bluestacks.bugzy.ui.login.OrganisationFrgment;
import com.bluestacks.bugzy.ui.login.ThemeSelectorFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBindingModule {
    @ContributesAndroidInjector
    abstract MyCasesFragment contributeSearchFragment();

    @ContributesAndroidInjector
    abstract PeopleFragment contributePeopleFragment();

    @ContributesAndroidInjector
    abstract CaseDetailsFragment contributeCaseDetailsFragment();

    @ContributesAndroidInjector
    abstract OrganisationFrgment contributeOrgFragment();

    @ContributesAndroidInjector
    abstract CredentialsFragment contributeCredentialsFragment();

    @ContributesAndroidInjector
    abstract ThemeSelectorFragment contributeThemeFragment();

    @ContributesAndroidInjector
    abstract KnowledgeFragment contributeKnowledgeFragment();

    @ContributesAndroidInjector
    abstract AboutActivityFragment contributeAboutFragment();
}
