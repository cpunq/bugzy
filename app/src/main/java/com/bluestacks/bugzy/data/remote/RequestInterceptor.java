package com.bluestacks.bugzy.data.remote;

import com.bluestacks.bugzy.data.local.PrefsHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


public class RequestInterceptor implements Interceptor {
    private PrefsHelper mPrefsHelper;

    public RequestInterceptor(PrefsHelper prefsHelper) {
        mPrefsHelper = prefsHelper;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = mPrefsHelper.getString(PrefsHelper.Key.ACCESS_TOKEN);
        if (TextUtils.isEmpty(token)) {
            // proceed as is
            return chain.proceed(chain.request());
        }
        RequestBody requestBody = this.processApplicationJsonRequestBody(chain.request().body(), token);
        Request.Builder requestBuilder = chain.request().newBuilder();
        Request newRequest = requestBuilder
                .post(requestBody)
                .build();
        return chain.proceed(newRequest);
    }

    private String bodyToString(final RequestBody request){
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if(copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }

    private RequestBody processApplicationJsonRequestBody(RequestBody requestBody,String token){
        String customReq = bodyToString(requestBody);
        try {
            JSONObject obj = new JSONObject(customReq);
            obj.put("token", token);
            return RequestBody.create(requestBody.contentType(), obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
