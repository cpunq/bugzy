package com.bluestacks.bugzy.data.remote;


import com.google.gson.Gson;

import com.bluestacks.bugzy.models.Resource;
import com.bluestacks.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class NetworkBoundTask<ResultType> implements Runnable {
    public MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    AppExecutors mAppExecutors;
    Gson mGson;

    @MainThread
    public NetworkBoundTask(AppExecutors appExecutors, Gson gson) {
        this.mAppExecutors = appExecutors;
        this.mGson = gson;
        result.setValue(Resource.loading(null));
    }

    @Override
    public void run() {
        Call<ResultType> call = createCall();
        try {
            Response<ResultType> response = call.execute();
            ApiResponse<ResultType> apiResponse = new ApiResponse<ResultType>(response, mGson);
            if (apiResponse.isSuccessful()) {
                result.postValue(Resource.success(apiResponse.body));
                saveCallResult(apiResponse.body);
            } else {
                result.postValue(Resource.error(apiResponse.errorMessage, null));
            }
        } catch (IOException e) {
            ApiResponse<ResultType> apiResponse = new ApiResponse<ResultType>(e, mGson);
            result.postValue(Resource.error(apiResponse.errorMessage, null));
        }
    }

    @WorkerThread
    protected ResultType processResponse(ApiResponse<ResultType> response) {
        return response.body;
    }

    @WorkerThread
    public abstract void saveCallResult(@NonNull ResultType result);

    @NonNull
    @WorkerThread
    protected abstract Call<ResultType> createCall();

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }
}
