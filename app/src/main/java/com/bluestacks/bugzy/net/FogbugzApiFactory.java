package com.bluestacks.bugzy.net;

import android.content.Context;

import com.bluestacks.bugzy.BuildConfig;
import com.bluestacks.bugzy.common.Const;

import org.simpleframework.xml.util.Cache;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class FogbugzApiFactory {



    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder ;

    private static FogbugzApiService fbClient;



    public static FogbugzApiService getApiClient(Context context) {
        if(builder == null){

            builder = new Retrofit.Builder()
                    .baseUrl(Const.API_BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(
                            SimpleXmlConverterFactory.create()
                    );

            Retrofit retrofit =
                    builder
                            .client(
                                    httpClient.addInterceptor(new ConnectivityInterceptor(context)).build()
                            )
                            .build();

            fbClient =  retrofit.create(FogbugzApiService.class);
        }
        return fbClient;
    }
}

