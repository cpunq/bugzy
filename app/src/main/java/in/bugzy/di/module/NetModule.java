package in.bugzy.di.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import in.bugzy.data.remote.ConnectivityInterceptor;
import in.bugzy.data.remote.FogbugzApiService;
import in.bugzy.data.remote.GithubApiService;
import in.bugzy.data.remote.HostSelectionInterceptor;
import in.bugzy.data.remote.RequestInterceptor;
import in.bugzy.data.local.PrefsHelper;
import in.bugzy.utils.LiveDataCallAdapterFactory;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provide network related modules.
 */
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

    @Provides
    @Singleton
    Gson provideGson() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();
        return gson;
    }

    @Provides
    @Singleton
    DatabaseHelper provideDatabaseHelper() {
        return new InMemoryDb();
    }

    @Provides
    @Singleton
    HostSelectionInterceptor provideHostSelectionInterceptor() {
        return new HostSelectionInterceptor();
    }

    @Provides @Singleton
    GithubApiService provideGithubApiService(Gson gson) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addCallAdapterFactory(new LiveDataCallAdapterFactory(gson))
                .addConverterFactory(GsonConverterFactory.create(gson));;
        Retrofit retrofit = builder.client(httpClientBuilder.build())
                .build();
        return retrofit.create(GithubApiService.class);
    }

    @Provides @Singleton
    FogbugzApiService provideFogBugzService(Application application, PrefsHelper prefsHelper, Gson gson, HostSelectionInterceptor hostSelectionInterceptor) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.writeTimeout(2, TimeUnit.MINUTES);
        httpClient.readTimeout(2, TimeUnit.MINUTES);


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addCallAdapterFactory(new LiveDataCallAdapterFactory(gson))
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder
                .client(
                        httpClient.addInterceptor(
                                new ConnectivityInterceptor(application.getApplicationContext())
                        )
                                .addInterceptor(new RequestInterceptor(prefsHelper))
                                .addInterceptor(hostSelectionInterceptor)
                                .build()
                )
                .build();
        return retrofit.create(FogbugzApiService.class);
    }
}
