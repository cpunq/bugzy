package com.bluestacks.bugzy.ui.home;


import com.bluestacks.bugzy.data.CasesRepository;
import com.bluestacks.bugzy.models.Resource;
import com.bluestacks.bugzy.models.resp.Case;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

public class MyCasesViewModel extends ViewModel {
    private CasesRepository mCasesRepository;
    private LiveData<Resource<List<Case>>> mCasesState;
    private MutableLiveData<String> mFilter = new MutableLiveData<>();

    @Inject
    MyCasesViewModel(CasesRepository casesRepository) {
        mCasesRepository = casesRepository;
        mFilter = new MutableLiveData<>();

        mCasesState = Transformations.switchMap(mFilter, filter -> {
            return mCasesRepository.cases(filter);
        });
    }

    public void loadCases(String filter) {
        mFilter.setValue(filter);
    }

    public LiveData<Resource<List<Case>>> getCasesState() {
        return mCasesState;
    }
}
