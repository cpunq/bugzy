package com.bluestacks.bugzy.ui.home;


import com.bluestacks.bugzy.data.Repository;
import com.bluestacks.bugzy.models.Resource;
import com.bluestacks.bugzy.models.resp.FiltersData;
import com.bluestacks.bugzy.models.resp.Person;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

public class HomeViewModel extends ViewModel {
    private MediatorLiveData<Boolean> mIsLoggedIn = new MediatorLiveData<>();
    private Repository mRepository;
    private MediatorLiveData<Resource<FiltersData>> mFiltersState = new MediatorLiveData<>();
    private MediatorLiveData<Resource<Person>> mMyDetailsState = new MediatorLiveData<>();

    @Inject
    HomeViewModel(Repository repository) {
        mRepository = repository;
        mIsLoggedIn = new MediatorLiveData<>();
        mFiltersState = new MediatorLiveData<>();
        mIsLoggedIn.addSource(mRepository.isLoggedIn(), loggedIn -> mIsLoggedIn.setValue(loggedIn));
        mFiltersState.addSource(mRepository.filters(), filtersDataResource -> mFiltersState.setValue(filtersDataResource));
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
