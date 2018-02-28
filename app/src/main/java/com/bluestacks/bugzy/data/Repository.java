package com.bluestacks.bugzy.data;


import com.google.gson.Gson;

import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.bluestacks.bugzy.data.remote.ApiResponse;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.data.remote.NetworkBoundResource;
import com.bluestacks.bugzy.data.remote.NetworkBoundTask;
import com.bluestacks.bugzy.models.Resource;
import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.LoginData;
import com.bluestacks.bugzy.models.resp.LoginRequest;
import com.bluestacks.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class Repository {
    private final AppExecutors mAppExecutors;
    private FogbugzApiService mApiService;
    private Gson mGson;
    private MutableLiveData<Boolean> mIsLoggedIn;
    private PrefsHelper mPrefs;

    @Inject
    Repository(AppExecutors appExecutors, FogbugzApiService apiService, Gson gson, PrefsHelper prefs) {
        mAppExecutors = appExecutors;
        mApiService = apiService;
        mGson = gson;
        mPrefs = prefs;
    }

    public LiveData<Resource<Response<LoginData>>> temp(String email, String password) {
        return new NetworkBoundResource<Response<LoginData>, Response<LoginData>>(mAppExecutors) {

            @Override
            protected void saveCallResult(@NonNull Response<LoginData> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable Response<LoginData> data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<Response<LoginData>> loadFromDb() {
                return new LiveData<Response<LoginData>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(null);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<LoginData>>> createCall() {
                return mApiService.loginWithEmail(new LoginRequest(email, password));
            }
        }.asLiveData();
    }

    public LiveData<Resource<Response<LoginData>>> login(String email, String password) {
        NetworkBoundTask<Response<LoginData>> task = new NetworkBoundTask<Response<LoginData>>(mAppExecutors, mGson) {
            @Override
            public void saveCallResult(@NonNull Response<LoginData> result) {
                String token = result.getData().getToken();
                mPrefs.setString(PrefsHelper.Key.ACCESS_TOKEN, token);
                mPrefs.setBoolean(PrefsHelper.Key.USER_LOGGED_IN, true);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        mIsLoggedIn.setValue(true);
                    }
                });
            }

            @NonNull
            @Override
            protected Call<Response<LoginData>> createCall() {
                return mApiService.login(new LoginRequest(email, password));
            }


        };
        mAppExecutors.networkIO().execute(task);
        return task.asLiveData();
    }

    public MutableLiveData<Boolean> isLoggedIn() {
        if (mIsLoggedIn != null) {
            return mIsLoggedIn;
        }
        mIsLoggedIn = new MutableLiveData<Boolean>() {
            @Override
            protected void onActive() {
                super.onActive();
                // Read from preferences
                setValue(!TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN)));
            }
        };
        return mIsLoggedIn;
    }
}
