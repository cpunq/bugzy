package in.bugzy.data.remote;


import com.google.gson.Gson;

import in.bugzy.data.model.Resource;
import in.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class NetworkBoundTask<ResponseType> implements Runnable {
    public MediatorLiveData<Resource<ResponseType>> result = new MediatorLiveData<>();

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
        Call<ResponseType> call = createCall();
        try {
            Response<ResponseType> response = call.execute();
            ApiResponse<ResponseType> apiResponse = new ApiResponse<ResponseType>(response, mGson);
            if (apiResponse.isSuccessful()) {
                saveCallResult(apiResponse.body);
                result.postValue(Resource.success(apiResponse.body));
            } else {
                result.postValue(Resource.error(apiResponse.errorMessage, null));
            }
        } catch (IOException e) {
            ApiResponse<ResponseType> apiResponse = new ApiResponse<ResponseType>(e, mGson);
            result.postValue(Resource.error(apiResponse.errorMessage, null));
        }
    }

    @WorkerThread
    protected ResponseType processResponse(ApiResponse<ResponseType> response) {
        return response.body;
    }

    @WorkerThread
    public abstract void saveCallResult(@NonNull ResponseType result);

    @NonNull
    @WorkerThread
    protected abstract Call<ResponseType> createCall();

    public LiveData<Resource<ResponseType>> asLiveData() {
        return result;
    }
}
