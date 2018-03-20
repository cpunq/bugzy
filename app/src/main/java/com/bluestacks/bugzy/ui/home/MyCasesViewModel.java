package com.bluestacks.bugzy.ui.home;


import com.bluestacks.bugzy.data.CasesRepository;
import com.bluestacks.bugzy.data.model.FilterCasesResult;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MyCasesViewModel extends ViewModel {
    private CasesRepository mCasesRepository;
    private LiveData<Resource<FilterCasesResult>> mCasesState;
    private MutableLiveData<Pair<String, List<String>>> mFilter = new MutableLiveData<>();
    private MutableLiveData<List<String>> mRemainingSortOrders = new MutableLiveData<>();

    private AppExecutors mAppExecutors;

    @Inject
    MyCasesViewModel(CasesRepository casesRepository, AppExecutors executors) {
        mCasesRepository = casesRepository;
        mAppExecutors = executors;
        mFilter = new MutableLiveData<>();

        mCasesState = Transformations.switchMap(mFilter, filter -> {
            // If the sort is changed t
            boolean sortChanged = false;
            if(filter.second != null) {
                sortChanged = true;
            }
            if (sortChanged) {
                MutableLiveData<Void> mediator = new MutableLiveData<>();
                // Save in db
                mAppExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mCasesRepository.saveSortOrder(filter.first, filter.second);
                        mediator.postValue(null);
                    }
                });

                // And then fetch cases with sortChanged = true
                return Transformations.switchMap(mediator, v -> {
                    return mCasesRepository.cases(filter.first, true);
                });
            }
            return mCasesRepository.cases(filter.first, false);
        });
    }

    public void onSortSelected(String sorting, int replacePosition) {
        List<String> newOrder = new ArrayList<>();
        if (mCasesState.getValue().data != null && mCasesState.getValue().data.getAppliedSortOrders() != null) {
            newOrder.addAll(mCasesState.getValue().data.getAppliedSortOrders());
        }
        if (replacePosition == -1) {
            // If not replacing, then add at last
            newOrder.add(sorting);
        } else {
            newOrder.remove(replacePosition);
            newOrder.add(replacePosition, sorting);
        }
        mFilter.setValue(new Pair<String, List<String>>(mFilter.getValue().first, newOrder));
    }

    public void removeSortClicked(int pos) {
        List<String> newOrder = new ArrayList<>();
        if (mCasesState.getValue().data != null && mCasesState.getValue().data.getAppliedSortOrders() != null) {
            newOrder.addAll(mCasesState.getValue().data.getAppliedSortOrders());
        }
        newOrder.remove(pos);
        mFilter.setValue(new Pair<String, List<String>>(mFilter.getValue().first, newOrder));
    }

    public List<String> getAvailableSortOrders() {
        return mCasesState.getValue().data.getAvailableSortOrders();
    }

    public void retryClicked() {
        mFilter.setValue(new Pair<String, List<String>>(mFilter.getValue().first, null));
    }

    public void loadCases(String filter) {
        mFilter.setValue(new Pair<>(filter, null));
    }

    public LiveData<Resource<FilterCasesResult>> getCasesState() {
        return mCasesState;
    }

}
