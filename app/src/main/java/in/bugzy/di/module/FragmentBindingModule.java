package in.bugzy.di.module;

import in.bugzy.ui.about.AboutActivityFragment;
import in.bugzy.ui.casedetails.CaseDetailsFragment;
import in.bugzy.ui.home.MyCasesFragment;
import in.bugzy.ui.home.PeopleFragment;
import in.bugzy.ui.login.CredentialsFragment;
import in.bugzy.ui.login.KnowledgeFragment;
import in.bugzy.ui.login.OrganisationFrgment;
import in.bugzy.ui.login.ThemeSelectorFragment;

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
