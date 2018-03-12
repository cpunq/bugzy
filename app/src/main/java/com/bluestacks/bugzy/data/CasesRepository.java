package com.bluestacks.bugzy.data;


import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.bluestacks.bugzy.data.local.db.BugzyDb;
import com.bluestacks.bugzy.data.local.db.CaseDao;
import com.bluestacks.bugzy.data.remote.ApiResponse;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.data.remote.NetworkBoundResource;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.remote.model.Response;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.FilterCasesResult;
import com.bluestacks.bugzy.data.remote.model.ListCasesData;
import com.bluestacks.bugzy.data.remote.model.ListCasesRequest;
import com.bluestacks.bugzy.data.remote.model.SearchCasesRequest;
import com.bluestacks.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CasesRepository {
    private AppExecutors mAppExecutors;
    private FogbugzApiService mApiService;
    private PrefsHelper mPrefs;
    private BugzyDb db;
    private CaseDao mCaseDao;


    @Inject
    CasesRepository(AppExecutors appExecutors, FogbugzApiService apiService, PrefsHelper prefs, CaseDao caseDao, BugzyDb database) {
        mAppExecutors = appExecutors;
        mApiService = apiService;
        mPrefs = prefs;
        mCaseDao = caseDao;
        db = database;
    }

    public LiveData<Resource<List<Case>>> cases(final String filter) {
        return new NetworkBoundResource<List<Case>, Response<ListCasesData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListCasesData> item) {
                db.beginTransaction();
                try {
                    mCaseDao.insertCases(item.getData().getCases());
                    mCaseDao.insert(new FilterCasesResult(filter, item.getData().getCaseIds()));
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Case> data) {
                // Always fetch for now
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Case>> loadFromDb() {
                return Transformations.switchMap(mCaseDao.loadCasesForFilter(filter), filterCasesData -> {
                    if (filterCasesData == null) {
                        return new LiveData<List<Case>>() {
                            @Override
                            protected void onActive() {
                                super.onActive();
                                setValue(null);
                            }
                        };
                    }
                    return mCaseDao.loadCasesById(filterCasesData.getCaseIds());
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListCasesData>>> createCall() {
                String[] cols =new String[]{
                        "sTitle","ixPriority","sStatus","sProject","sPersonAssignedTo","sPersonOpenedBy"
                };
                ListCasesRequest request = new ListCasesRequest(cols, filter);
                return mApiService.listCases(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Case>> caseDetails(final Case kase) {
        return new NetworkBoundResource<Case, Response<ListCasesData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListCasesData> item) {
                db.beginTransaction();
                try {
                    mCaseDao.insert(item.getData().getCases().get(0));
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Case data) {
                // Fetch always
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Case> loadFromDb() {
                return mCaseDao.loadCaseById(kase.getIxBug());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListCasesData>>> createCall() {
                String[] cols =new String[]{
                        "sTitle","ixPriority","sStatus","sProject","sFixFor","sArea","sPersonAssignedTo","sPersonOpenedBy","events"
                };
                return mApiService.searchCases(new SearchCasesRequest(cols, kase.getIxBug()+""));
            }
        }.asLiveData();
    }
}
