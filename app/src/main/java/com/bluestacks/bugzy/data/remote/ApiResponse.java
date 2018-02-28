package com.bluestacks.bugzy.data.remote;

import com.google.gson.Gson;

import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.models.Error;

import android.support.annotation.Nullable;
import java.io.IOException;


import retrofit2.Response;

/**
 * Common class used by API responses.
 * @param <T>
 */
public class ApiResponse<T> {
    public final int httpCode;
    @Nullable
    public T body;
    @Nullable
    public String errorMessage;

    public int fbCode;

    private Gson mGson;

    public ApiResponse(Throwable error, Gson gson) {
        httpCode = 500;
        fbCode = Const.UNKNOWN_ERROR;
        body = null;
        errorMessage = error.getMessage();

        if (error instanceof ConnectivityInterceptor.NoConnectivityException) {
            errorMessage = "Please check your connection";
            fbCode = Const.NO_NETWORK;
        } else if (error instanceof IOException) {
            errorMessage = "Oops! We can't reach Fogbugz";
            fbCode = Const.NETWORK_ERROR;
        }
        mGson = gson;
    }

    public ApiResponse(Response<T> response, Gson gson) {
        mGson = gson;
        httpCode = response.code();
        if(response.isSuccessful()) {
            body = response.body();
            errorMessage = null;
        } else {
            if (response.errorBody() != null) {
                try {
                    String stringbody = response.errorBody().string();
                    body = mGson.fromJson(stringbody, (Class<T>) com.bluestacks.bugzy.models.Response.class);
                    Error e = (Error)((com.bluestacks.bugzy.models.Response)body).getErrors().get(0);
                    errorMessage  = e.getMessage();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                    errorMessage = "Oops! We can't reach Fogbugz";
                    fbCode = Const.NETWORK_ERROR;
                    body = null;
//                    Timber.e(ignored, "error while parsing response");
                    //network error
                }
            } else {
                errorMessage = "Oops! We can't reach Fogbugz";
                fbCode = Const.NETWORK_ERROR;
                body = null;
            }
        }
    }

    public boolean isSuccessful() {
        return httpCode >= 200 && httpCode < 300;
    }
}
