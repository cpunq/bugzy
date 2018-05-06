package in.bugzy.data.remote;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import in.bugzy.common.Const;
import in.bugzy.data.remote.model.Error;

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
                    body = mGson.fromJson(stringbody, (Class<T>) in.bugzy.data.remote.model.Response.class);
                    Error e = (Error)((in.bugzy.data.remote.model.Response)body).getErrors().get(0);
                    errorMessage  = e.getMessage();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                    errorMessage = "Oops! We can't reach Fogbugz";
                    fbCode = Const.NETWORK_ERROR;
                    body = null;
//                    Timber.e(ignored, "error while parsing response");
                    //network error
                } catch (JsonSyntaxException syntaxException) {
                    // If theres a syntax exception lets treat it as a network exception as of now
                    errorMessage = "Oops! We can't reach Fogbugz";
                    fbCode = Const.NETWORK_ERROR;
                    body = null;
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
