package com.bluestacks.bugzy.ui.search;

import com.bluestacks.bugzy.data.CasesRepository;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.SearchResultsResource;
import com.bluestacks.bugzy.utils.SingleLiveEvent;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;


public class SearchActivityViewModel extends ViewModel {
    private CasesRepository mCasesRepository;
    private MutableLiveData<String> mSearchLiveData = new MutableLiveData<>();
    private MediatorLiveData<SearchResultsResource<List<Case>>> mSearchResponse;
    private SingleLiveEvent<Void> mClearSearchEvent = new SingleLiveEvent<>();
    private String mCurrentQuery = null;

    @Inject
    public SearchActivityViewModel(CasesRepository repository) {
        mCasesRepository = repository;
        LiveData<SearchResultsResource<List<Case>>> tempLiveData = Transformations
                .switchMap(mSearchLiveData, value -> {
                    return mCasesRepository.searchCases(value);
                });
        mSearchResponse = new MediatorLiveData<>();
        mSearchResponse.addSource(tempLiveData, v -> {
            if (v.getQuery() == mCurrentQuery) {
                // If the query is equal to the current query, only then dispatch the event
                mSearchResponse.setValue(v);
            }
        });
    }

    public void search(String query) {
        mCurrentQuery = query;
        mSearchLiveData.setValue(query);
    }

    public LiveData<SearchResultsResource<List<Case>>> getSearchResponse() {
        return mSearchResponse;
    }

    public void clearSearch() {
        mCurrentQuery = null;
        mClearSearchEvent.setValue(null);
    }

    public SingleLiveEvent<Void> getClearSearchEvent() {
        return mClearSearchEvent;
    }
}
