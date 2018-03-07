package com.bluestacks.bugzy.data;


import com.google.gson.Gson;

import com.bluestacks.bugzy.data.local.DatabaseHelper;
import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.bluestacks.bugzy.data.remote.ApiResponse;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.data.remote.NetworkBoundResource;
import com.bluestacks.bugzy.models.Resource;
import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.ListCasesData;
import com.bluestacks.bugzy.models.resp.ListCasesRequest;
import com.bluestacks.bugzy.models.resp.SearchCasesRequest;
import com.bluestacks.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CasesRepository {
    private AppExecutors mAppExecutors;
    private FogbugzApiService mApiService;
    private Gson mGson;
    private PrefsHelper mPrefs;
    private DatabaseHelper mDbHelper;


    @Inject
    CasesRepository(AppExecutors appExecutors, FogbugzApiService apiService, Gson gson, PrefsHelper prefs, DatabaseHelper dbHelper) {
        mAppExecutors = appExecutors;
        mApiService = apiService;
        mGson = gson;
        mPrefs = prefs;
        mDbHelper = dbHelper;
    }

    public LiveData<Resource<List<Case>>> cases(final String filter) {
        return new NetworkBoundResource<List<Case>, Response<ListCasesData>>(mAppExecutors) {
            // Mocking the local db
            List<Case> mCases;
            @Override
            protected void saveCallResult(@NonNull Response<ListCasesData> item) {
                // Not saving as of now
                mCases = item.getData().getCases();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Case> data) {
                // Always fetch for now
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Case>> loadFromDb() {
                return new LiveData<List<Case>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(mCases);
                    }
                };
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

    public LiveData<Resource<Case>> caseDetails(final int caseId) {
        return new NetworkBoundResource<Case, Response<ListCasesData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListCasesData> item) {

            }

            @Override
            protected boolean shouldFetch(@Nullable Case data) {
                // Fetch always
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Case> loadFromDb() {
                return new LiveData<Case>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(null);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListCasesData>>> createCall() {
                String[] cols =new String[]{
                        "sTitle","ixPriority","sStatus","sProject","sFixFor","sArea","sPersonAssignedTo","sPersonOpenedBy","events"
                };
                return mApiService.searchCases(new SearchCasesRequest(cols, caseId+""));
            }
        }.asLiveData();
    }
}
