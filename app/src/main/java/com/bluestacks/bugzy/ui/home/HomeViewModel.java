package com.bluestacks.bugzy.ui.home;


import com.bluestacks.bugzy.data.Repository;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.data.remote.model.FiltersData;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.utils.BugzyDataSyncService;
import com.crashlytics.android.Crashlytics;

import android.app.Application;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

public class HomeViewModel extends ViewModel {
    private Repository mRepository;
    private MediatorLiveData<Boolean> mIsLoggedIn = new MediatorLiveData<>();
    private MediatorLiveData<Resource<FiltersData>> mFiltersState = new MediatorLiveData<>();
    private MediatorLiveData<Resource<Person>> mMyDetailsState = new MediatorLiveData<>();
    private Context mContext;
    private boolean mSyncServiceStartedOnce;

    @Inject
    HomeViewModel(Repository repository, Application application) {
        mRepository = repository;
        mContext = application.getApplicationContext();
        mIsLoggedIn.addSource(mRepository.getToken(), token -> {
            if (token == null) {
                mIsLoggedIn.setValue(false);
            } else {
                mIsLoggedIn.setValue(true);
            }
        });
        mFiltersState.addSource(mRepository.filters(), filtersDataResource -> {
            if (filtersDataResource.status == Status.SUCCESS && !mSyncServiceStartedOnce) {
                // When filters are received, immediately start syncing other data
                // For now, lets only do it once for the lifetime of this activity
                mContext.startService(new Intent(mContext, BugzyDataSyncService.class));
                mSyncServiceStartedOnce = true;
            }
            mFiltersState.setValue(filtersDataResource);
        });
        mMyDetailsState.addSource(mRepository.getMyDetails(), personResource -> {
            // Setting CrashLytics user
            if (personResource.status == Status.SUCCESS) {
                Crashlytics.setUserIdentifier(personResource.data.getPersonid()+ "");
                Crashlytics.setUserEmail(personResource.data.getEmail());
                Crashlytics.setUserName(personResource.data.getFullname());
            }
            mMyDetailsState.setValue(personResource);
        });
    }

    public void logout() {
        mRepository.logout();
    }

    public MediatorLiveData<Boolean> getIsLoggedIn() {
        return mIsLoggedIn;
    }

    public MediatorLiveData<Resource<FiltersData>> getFiltersState() {
        return mFiltersState;
    }

    public MediatorLiveData<Resource<Person>> getMyDetailsState() {
        return mMyDetailsState;
    }
}
