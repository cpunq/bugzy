package com.bluestacks.bugzy.di;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.bluestacks.bugzy.net.ConnectivityInterceptor;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.net.RequestInterceptor;
import com.bluestacks.bugzy.utils.PrefsHelper;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {
    private final String mBaseUrl;

    public NetModule(String url) {
        mBaseUrl = url;
    }

    @Provides @Singleton
    PrefsHelper provicePreferenceHelper(Application application) {
        return new PrefsHelper(application.getApplicationContext());
    }

    @Provides @Singleton
    FogbugzApiService provideFogBugzService(Application application, PrefsHelper prefsHelper) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder
                .client(
                        httpClient.addInterceptor(
                                new ConnectivityInterceptor(application.getApplicationContext())
                        )
                                .addInterceptor(new RequestInterceptor(prefsHelper))
                                .build()
                )
                .build();
        return retrofit.create(FogbugzApiService.class);
    }
}
