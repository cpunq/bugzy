package com.bluestacks.bugzy.ui.home;


import com.bluestacks.bugzy.data.Repository;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.data.remote.model.FiltersData;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.utils.BugzyDataSyncService;

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
            if (filtersDataResource.status == Status.SUCCESS) {
                // When filters are received, immediately start syncing other data
                mContext.startService(new Intent(mContext, BugzyDataSyncService.class));
            }
            mFiltersState.setValue(filtersDataResource);
        });
        mMyDetailsState.addSource(mRepository.getMyDetails(), personResource -> mMyDetailsState.setValue(personResource));
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
