package com.bluestacks.bugzy.data;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import com.bluestacks.bugzy.data.local.DatabaseHelper;
import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.bluestacks.bugzy.data.remote.ApiResponse;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.data.remote.NetworkBoundResource;
import com.bluestacks.bugzy.data.remote.NetworkBoundTask;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.remote.model.Response;
import com.bluestacks.bugzy.data.model.Filter;
import com.bluestacks.bugzy.data.remote.model.FiltersData;
import com.bluestacks.bugzy.data.remote.model.FiltersRequest;
import com.bluestacks.bugzy.data.remote.model.ListPeopleData;
import com.bluestacks.bugzy.data.remote.model.ListPeopleRequest;
import com.bluestacks.bugzy.data.remote.model.LoginData;
import com.bluestacks.bugzy.data.remote.model.LoginRequest;
import com.bluestacks.bugzy.data.remote.model.MyDetailsData;
import com.bluestacks.bugzy.data.remote.model.MyDetailsRequest;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class Repository {
    private final AppExecutors mAppExecutors;
    private FogbugzApiService mApiService;
    private Gson mGson;
    private MutableLiveData<String> mToken;
    private PrefsHelper mPrefs;
    private DatabaseHelper mDbHelper;

    @Inject
    Repository(AppExecutors appExecutors, FogbugzApiService apiService, Gson gson, PrefsHelper prefs, DatabaseHelper dbHelper) {
        mAppExecutors = appExecutors;
        mApiService = apiService;
        mGson = gson;
        mPrefs = prefs;
        mDbHelper = dbHelper;

        mToken = new MutableLiveData<String>() {
            @Override
            protected void onActive() {
                super.onActive();
                // Read from preferences
                String token = mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN);

                if ("".equals(token)) {
                    setValue(null);
                } else {
                    setValue(token);
                }

            }
        };
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
                        mToken.setValue(token);
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

    public LiveData<Resource<FiltersData>> filters() {
        return new NetworkBoundResource<FiltersData, Response<JsonElement>> (mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<JsonElement> item) {
                JsonElement body = item.getData();
                JsonArray filtersjson = body.getAsJsonObject().getAsJsonArray("filters");
                final List<Filter> filters = new ArrayList<>();
                for (int i = 0 ; i < filtersjson.size() ; i++) {
                    JsonElement d = filtersjson.get(i);
                    try {
                        Filter f = mGson.fromJson(d, Filter.class);
                        // Set it on disk
                        filters.add(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mPrefs.setString(PrefsHelper.Key.FILTERS_LIST, mGson.toJson(filters));
            }

            @Override
            protected boolean shouldFetch(@Nullable FiltersData data) {
                // Always refresh filters
                return true;
            }

            @NonNull
            @Override
            protected LiveData<FiltersData> loadFromDb() {
                return new LiveData<FiltersData>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        String filterString = mPrefs.getString(PrefsHelper.Key.FILTERS_LIST);
                        if (TextUtils.isEmpty(filterString)) {
                            setValue(null);
                            return;
                        }
                        Type typeOfObjectsList = new TypeToken<ArrayList<Filter>>() {}.getType();
                        List<Filter> filters = mGson.fromJson(filterString, typeOfObjectsList);
                        FiltersData d = new FiltersData();
                        d.setFilters(filters);
                        setValue(d);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<JsonElement>>> createCall() {
                return mApiService.getFilters(new FiltersRequest());
            }
        }.asLiveData();
    }

    public LiveData<Resource<Person>> getMyDetails() {
        return new NetworkBoundResource<Person, Response<MyDetailsData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<MyDetailsData> item) {
                Person me = item.getData().getPerson();
                mPrefs.setString(PrefsHelper.Key.USER_NAME, me.getFullname());
                mPrefs.setString(PrefsHelper.Key.USER_EMAIL, me.getEmail());
                mPrefs.setInt(PrefsHelper.Key.PERSON_ID, me.getPersonid());
            }

            @Override
            protected boolean shouldFetch(@Nullable Person data) {
                // Only fetch once
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<Person> loadFromDb() {
                return new LiveData<Person>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        if (TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.USER_EMAIL))) {
                            setValue(null);
                            return;
                        }
                        Person me = new Person();
                        me.setFullname(mPrefs.getString(PrefsHelper.Key.USER_NAME));
                        me.setPersonid(mPrefs.getInt(PrefsHelper.Key.PERSON_ID));
                        me.setEmail(mPrefs.getString(PrefsHelper.Key.USER_EMAIL));
                        setValue(me);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<MyDetailsData>>> createCall() {
                return mApiService.getMyDetails(new MyDetailsRequest());
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Person>>> getPeople() {
        return new NetworkBoundResource<List<Person>, Response<ListPeopleData>>(mAppExecutors) {
            List<Person> mPersons;
            @Override
            protected void saveCallResult(@NonNull Response<ListPeopleData> item) {
                mPersons = item.getData().getPersons();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Person> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Person>> loadFromDb() {
                return new LiveData<List<Person>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(mPersons);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListPeopleData>>> createCall() {
                return mApiService.listPeople(new ListPeopleRequest());
            }
        }.asLiveData();
    }

    public MutableLiveData<String> getToken() {
        return mToken;
    }
}
