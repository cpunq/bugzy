package com.bluestacks.bugzy.ui.home;


import com.bluestacks.bugzy.data.CasesRepository;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.model.Case;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

public class MyCasesViewModel extends ViewModel {
    private CasesRepository mCasesRepository;
    private LiveData<Resource<List<Case>>> mCasesState;
    private MutableLiveData<String> mFilter = new MutableLiveData<>();
    private MutableLiveData<List<String>> mAppliedSorting = new MutableLiveData<>();


    @Inject
    MyCasesViewModel(CasesRepository casesRepository) {
        mCasesRepository = casesRepository;
        mFilter = new MutableLiveData<>();

        mAppliedSorting.setValue(casesRepository.getSortingOrders());

        mCasesState = Transformations.switchMap(mFilter, filter -> {
            return mCasesRepository.cases(filter, null);
        });
    }

    public void onSortSelected(String sorting) {
    }

    public void loadCases(String filter) {
        mFilter.setValue(filter);
    }

    public LiveData<Resource<List<Case>>> getCasesState() {
        return mCasesState;
    }

    public MutableLiveData<List<String>> getAppliedSorting() {
        return mAppliedSorting;
    }
}
