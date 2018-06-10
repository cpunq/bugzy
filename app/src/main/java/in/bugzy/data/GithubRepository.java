package in.bugzy.data;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import in.bugzy.data.local.PrefsHelper;
import in.bugzy.data.model.GitUser;
import in.bugzy.data.model.Resource;
import in.bugzy.data.remote.ApiResponse;
import in.bugzy.data.remote.GithubApiService;
import in.bugzy.data.remote.NetworkBoundResource;
import in.bugzy.utils.AppExecutors;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GithubRepository {
    private GithubApiService mGithubApiService;
    private AppExecutors mAppExecutors;
    private PrefsHelper mPrefsHelper;
    private Gson mGson;
    private boolean mContributorsFetched = false;

    @Inject
    GithubRepository(GithubApiService apiService, AppExecutors executors, PrefsHelper prefsHelper, Gson gson) {
        mGithubApiService = apiService;
        mAppExecutors = executors;
        mPrefsHelper = prefsHelper;
        mGson = gson;
    }

    public LiveData<Resource<List<GitUser>>> getContributors(boolean forceFetch) {
        return new NetworkBoundResource<List<GitUser>, List<GitUser>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<GitUser> item) {
                mContributorsFetched = true;
                mPrefsHelper.setString(PrefsHelper.Key.CONTRIBUTORS_JSON, mGson.toJson(item));
            }

            @Override
            protected boolean shouldFetch(@Nullable List<GitUser> data) {
                if (forceFetch) {
                    return true;
                }
                if (data == null) {
                    return true;
                }
                if (mContributorsFetched) {
                    // Not fetching if fetched once per application instance
                    return false;
                }
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<GitUser>> loadFromDb() {
                return new LiveData<List<GitUser>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        String v = mPrefsHelper.getString(PrefsHelper.Key.CONTRIBUTORS_JSON);
                        if (TextUtils.isEmpty(v)) {
                            setValue(null);
                            return;
                        }
                        Type typeOfUserList = new TypeToken<List<GitUser>>() {}.getType();
                        List<GitUser> users = mGson.fromJson(v, typeOfUserList);
                        setValue(users);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<GitUser>>> createCall() {
                return mGithubApiService.getBugzyContributors();
            }
        }.asLiveData();
    }
}
