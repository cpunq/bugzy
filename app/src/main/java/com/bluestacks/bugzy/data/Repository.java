package com.bluestacks.bugzy.data;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.bluestacks.bugzy.data.local.db.BugzyDb;
import com.bluestacks.bugzy.data.local.db.MiscDao;
import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.CaseStatus;
import com.bluestacks.bugzy.data.model.Category;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Priority;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.data.model.Tag;
import com.bluestacks.bugzy.data.remote.ApiResponse;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.data.remote.HostSelectionInterceptor;
import com.bluestacks.bugzy.data.remote.NetworkBoundResource;
import com.bluestacks.bugzy.data.remote.NetworkBoundTask;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.remote.model.ClearBitCompanyInfo;
import com.bluestacks.bugzy.data.remote.model.ListAreasData;
import com.bluestacks.bugzy.data.remote.model.ListCategoriesData;
import com.bluestacks.bugzy.data.remote.model.ListMilestonesData;
import com.bluestacks.bugzy.data.remote.model.ListPrioritiesData;
import com.bluestacks.bugzy.data.remote.model.ListProjectsData;
import com.bluestacks.bugzy.data.remote.model.ListStatusesData;
import com.bluestacks.bugzy.data.remote.model.ListTagsData;
import com.bluestacks.bugzy.data.remote.model.Request;
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
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
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
    private MiscDao mMiscDao;
    private BugzyDb db;
    private SearchSuggestionRepository mSsRespository;
    private HostSelectionInterceptor mHostSelectionInterceptor;


    private MediatorLiveData<Resource<List<Area>>> mAreasPublicLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Project>>> mProjectsPublicLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Milestone>>> mMilestonesPublicLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Person>>> mPeoplePublicLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Priority>>> mPrioritiesPublicLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<CaseStatus>>> mStatusesPublicLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Category>>> mCategoriesPublicLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<Tag>>> mTagsPublicLiveData = new MediatorLiveData<>();


    private LiveData<Resource<List<Area>>> mFetchAreasLiveData;
    private LiveData<Resource<List<Project>>> mFetchProjectsLiveData;
    private LiveData<Resource<List<Milestone>>> mFetchMilestonesLiveData;
    private LiveData<Resource<List<Person>>> mFetchPeopleLiveData;
    private LiveData<Resource<List<Priority>>> mFetchPrioritiesLiveData;
    private LiveData<Resource<List<Category>>> mFetchCategoriesLiveData;
    private LiveData<Resource<List<CaseStatus>>> mFetchStatusesLiveData;
    private LiveData<Resource<List<Tag>>> mFetchTagsLiveData;

    @Inject
    Repository(AppExecutors appExecutors, FogbugzApiService apiService, Gson gson, PrefsHelper prefs, MiscDao miscDao, BugzyDb databaseObject, SearchSuggestionRepository ssRepository, HostSelectionInterceptor interceptor) {
        mAppExecutors = appExecutors;
        mApiService = apiService;
        mGson = gson;
        mPrefs = prefs;
        mMiscDao = miscDao;
        db = databaseObject;
        mSsRespository = ssRepository;
        mHostSelectionInterceptor = interceptor;

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

    public void logout() {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // Remove everything from db and preferences
                db.clearAllTables();
                mPrefs.clearAll();
                mToken.postValue(null);
            }
        });
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

    public LiveData<Resource<Response<LoginData>>> login(String email, String password, String organisation) {
        mHostSelectionInterceptor.setHost(organisation.toLowerCase()+".manuscript.com");

        NetworkBoundTask<Response<LoginData>> task = new NetworkBoundTask<Response<LoginData>>(mAppExecutors, mGson) {
            @Override
            public void saveCallResult(@NonNull Response<LoginData> result) {
                String token = result.getData().getToken();
                mPrefs.setString(PrefsHelper.Key.ORGANISATION, organisation.toLowerCase());
                mPrefs.setString(PrefsHelper.Key.ACCESS_TOKEN, token);
                mPrefs.setBoolean(PrefsHelper.Key.USER_LOGGED_IN, true);
                mSsRespository.insertDefaultSearchSuggestions();
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

    public LiveData<Resource<List<ClearBitCompanyInfo>>> getCompanyLogo(String query) {
        NetworkBoundTask<List<ClearBitCompanyInfo>> task =  new NetworkBoundTask<List<ClearBitCompanyInfo>>(mAppExecutors, mGson) {
            @Override
            public void saveCallResult(@NonNull List<ClearBitCompanyInfo> result) {
            }

            @NonNull
            @Override
            protected Call<List<ClearBitCompanyInfo>> createCall() {
                return mApiService.getCompanyLogo("https://autocomplete.clearbit.com/v1/companies/suggest", query);
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

    public LiveData<Resource<List<Person>>> getPeople(boolean mustFetch) {
        if (mFetchPeopleLiveData != null) {
            if (mFetchPeopleLiveData.getValue().status == Status.LOADING && !mustFetch) {
                // If the content is in loading state and the request doesn't require us to fetch again
                return mPeoplePublicLiveData;
            }
            mPeoplePublicLiveData.removeSource(mFetchPeopleLiveData);
            mFetchPeopleLiveData = null;
        }
        mFetchPeopleLiveData = new NetworkBoundResource<List<Person>, Response<ListPeopleData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListPeopleData> item) {
                mSsRespository.updatePeopleSearchSuggestion(item.getData().getPersons());
                try {
                    db.beginTransaction();
                    db.miscDao().insertPersons(item.getData().getPersons());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Person> data) {
                if (data == null || data.size() == 0) {
                    return true;
                }
                if (mustFetch) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Person>> loadFromDb() {
                return mMiscDao.loadPersons();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListPeopleData>>> createCall() {
                return mApiService.listPeople(new ListPeopleRequest());
            }
        }.asLiveData();
        mPeoplePublicLiveData.addSource(mFetchPeopleLiveData, value -> {
            mPeoplePublicLiveData.setValue(value);
        });
        return mPeoplePublicLiveData;
    }

    public MediatorLiveData<Resource<List<Area>>> getAreas(boolean mustFetch) {
        if (mFetchAreasLiveData != null) {
            if (mFetchAreasLiveData.getValue().status == Status.LOADING && !mustFetch) {
                // If the content is in loading state and the request doesn't require us to fetch again
                return mAreasPublicLiveData;
            }
            mAreasPublicLiveData.removeSource(mFetchAreasLiveData);
            mFetchAreasLiveData = null;
        }
        mFetchAreasLiveData = new NetworkBoundResource<List<Area>, Response<ListAreasData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListAreasData> item) {
                mSsRespository.updateAreaSearchSuggestion(item.getData().getAreas());
                db.beginTransaction();
                try {
                    mMiscDao.insert(item.getData().getAreas());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Area> data) {
                if (data == null || data.size() == 0) {
                    return true;
                }
                if (mustFetch) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Area>> loadFromDb() {
                return mMiscDao.loadAreas();
            };

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListAreasData>>> createCall() {
                return mApiService.getAreas(new Request("listAreas"));
            }
        }.asLiveData();

        mAreasPublicLiveData.addSource(mFetchAreasLiveData, value -> {
            mAreasPublicLiveData.setValue(value);
        });
        return mAreasPublicLiveData;
    }


    public LiveData<Resource<List<Milestone>>> getMilestones(int projectId) {
        return Transformations.switchMap(getMilestones(false), mileStonesResource -> {
            if(mileStonesResource.data == null || mileStonesResource.data.size() == 0) {
                return mileStonesResource.asLiveData();
            }
            // If the milestones are fetched, then get milestones for this project
            return Transformations.map(mMiscDao.loadMilestones(projectId), mileStonesForProject -> {
                return new Resource<List<Milestone>>(mileStonesResource.status, mileStonesForProject, mileStonesResource.message);
            });
        });
    }

    public LiveData<Resource<List<Area>>> getAreas(int projectId) {
        return Transformations.switchMap(getAreas(false), alreasResource -> {
            if(alreasResource.data == null || alreasResource.data.size() == 0) {
                return alreasResource.asLiveData();
            }
            // If the milestones are fetched, then get milestones for this project
            return Transformations.map(mMiscDao.loadAreas(projectId), areasForProject -> {
                return new Resource<List<Area>>(alreasResource.status, areasForProject, alreasResource.message);
            });
        });
    }

    public LiveData<Resource<List<CaseStatus>>> getStatuses(int categoryId) {
        return Transformations.switchMap(getStatuses(false), statusResource -> {
            if(statusResource.data == null || statusResource.data.size() == 0) {
                return statusResource.asLiveData();
            }
            // If the milestones are fetched, then get milestones for this project
            return Transformations.map(mMiscDao.loadStatuses(categoryId),
                    statusForCategory -> new Resource<>(statusResource.status, statusForCategory, statusResource.message));
        });
    }

    public LiveData<Resource<List<Milestone>>> getMilestones(boolean mustFetch) {
        if (mFetchMilestonesLiveData != null) {
            if (mFetchMilestonesLiveData.getValue().status == Status.LOADING && !mustFetch) {
                // If the content is in loading state and the request doesn't require us to fetch again
                return mMilestonesPublicLiveData;
            }
            mMilestonesPublicLiveData.removeSource(mFetchMilestonesLiveData);
            mFetchMilestonesLiveData = null;
        }
        mFetchMilestonesLiveData = new NetworkBoundResource<List<Milestone>, Response<ListMilestonesData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListMilestonesData> item) {
                mSsRespository.updateMilestoneSearchSuggestion(item.getData().getMilestones());
                db.beginTransaction();
                try {
                    mMiscDao.insertMilestones(item.getData().getMilestones());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Milestone> data) {
                if (data == null || data.size() == 0) {
                    return true;
                }
                if (mustFetch) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Milestone>> loadFromDb() {
                return mMiscDao.loadMilestones();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListMilestonesData>>> createCall() {
                return mApiService.getMilestones(new Request("listFixFors"));
            }
        }.asLiveData();
        mMilestonesPublicLiveData.addSource(mFetchMilestonesLiveData, value -> {
            mMilestonesPublicLiveData.setValue(value);
        });
        return mMilestonesPublicLiveData;
    }

    public LiveData<Resource<List<Project>>> getProjects(boolean mustFetch) {
        if (mFetchProjectsLiveData != null) {
            if (mFetchProjectsLiveData.getValue().status == Status.LOADING && !mustFetch) {
                // If the content is in loading state and the request doesn't require us to fetch again
                return mProjectsPublicLiveData;
            }
            mProjectsPublicLiveData.removeSource(mFetchProjectsLiveData);
            mFetchProjectsLiveData = null;
        }
        mFetchProjectsLiveData = new NetworkBoundResource<List<Project>, Response<ListProjectsData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListProjectsData> item) {
                db.beginTransaction();
                try {
                    mMiscDao.insertProjects(item.getData().getProjects());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Project> data) {
                if (data == null || data.size() == 0) {
                    return true;
                }
                if (mustFetch) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Project>> loadFromDb() {
                return mMiscDao.loadProjects();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListProjectsData>>> createCall() {
                return mApiService.getProjects(new Request("listProjects"));
            }
        }.asLiveData();
        mProjectsPublicLiveData.addSource(mFetchProjectsLiveData, value -> {
            mProjectsPublicLiveData.setValue(value);
        });
        return mProjectsPublicLiveData;
    }

    public MutableLiveData<String> getToken() {
        return mToken;
    }

    public LiveData<Resource<List<Priority>>> getPriorities(boolean mustFetch) {
        if (mFetchPrioritiesLiveData != null) {
            if (mFetchPrioritiesLiveData.getValue().status == Status.LOADING && !mustFetch) {
                // If the content is in loading state and the request doesn't require us to fetch again
                return mPrioritiesPublicLiveData;
            }
            mPrioritiesPublicLiveData.removeSource(mFetchPrioritiesLiveData);
            mFetchPrioritiesLiveData = null;
        }
        mFetchPrioritiesLiveData = new NetworkBoundResource<List<Priority>, Response<ListPrioritiesData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListPrioritiesData> item) {
                db.beginTransaction();
                try {
                    mMiscDao.insertPriorities(item.getData().getPriorities());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Priority> data) {
                if (data == null || data.size() == 0) {
                    return true;
                }
                if (mustFetch) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Priority>> loadFromDb() {
                return mMiscDao.loadPriorities();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListPrioritiesData>>> createCall() {
                return mApiService.getPriorities(new Request("listPriorities"));
            }
        }.asLiveData();
        mPrioritiesPublicLiveData.addSource(mFetchPrioritiesLiveData, value -> {
            mPrioritiesPublicLiveData.setValue(value);
        });
        return mPrioritiesPublicLiveData;
    }

    public LiveData<Resource<List<Category>>> getCategories(boolean mustFetch) {
        if (mFetchCategoriesLiveData != null) {
            if (mFetchCategoriesLiveData.getValue().status == Status.LOADING && !mustFetch) {
                // If the content is in loading state and the request doesn't require us to fetch again
                return mCategoriesPublicLiveData;
            }
            mCategoriesPublicLiveData.removeSource(mFetchCategoriesLiveData);
            mFetchCategoriesLiveData = null;
        }
        mFetchCategoriesLiveData = new NetworkBoundResource<List<Category>, Response<ListCategoriesData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListCategoriesData> item) {
                db.beginTransaction();
                try {
                    mMiscDao.insertCategories(item.getData().getCategories());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Category> data) {
                if (data == null || data.size() == 0) {
                    return true;
                }
                if (mustFetch) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Category>> loadFromDb() {
                return mMiscDao.loadCategories();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListCategoriesData>>> createCall() {
                return mApiService.getCategories(new Request("listCategories"));
            }
        }.asLiveData();
        mCategoriesPublicLiveData.addSource(mFetchCategoriesLiveData, value -> {
            mCategoriesPublicLiveData.setValue(value);
        });
        return mCategoriesPublicLiveData;
    }

    public LiveData<Resource<List<CaseStatus>>> getStatuses(boolean mustFetch) {
        if (mFetchStatusesLiveData != null) {
            if (mFetchStatusesLiveData.getValue().status == Status.LOADING && !mustFetch) {
                // If the content is in loading state and the request doesn't require us to fetch again
                return mStatusesPublicLiveData;
            }
            mStatusesPublicLiveData.removeSource(mFetchStatusesLiveData);
            mFetchStatusesLiveData = null;
        }
        mFetchStatusesLiveData = new NetworkBoundResource<List<CaseStatus>, Response<ListStatusesData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListStatusesData> item) {
                db.beginTransaction();
                try {
                    mMiscDao.insertStatuses(item.getData().getStatuses());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<CaseStatus> data) {
                if (data == null || data.size() == 0) {
                    return true;
                }
                if (mustFetch) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<CaseStatus>> loadFromDb() {
                return mMiscDao.loadStatuses();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListStatusesData>>> createCall() {
                return mApiService.getStatuses(new Request("listStatuses"));
            }
        }.asLiveData();
        mStatusesPublicLiveData.addSource(mFetchStatusesLiveData, value -> {
            mStatusesPublicLiveData.setValue(value);
        });
        return mStatusesPublicLiveData;
    }

    public LiveData<Resource<List<Tag>>> searchTags(String query) {
        return Transformations.switchMap(getTags(false), alreasResource -> {
            if(alreasResource.data == null || alreasResource.data.size() == 0) {
                return alreasResource.asLiveData();
            }
            // If the tags are fetched, then get tags for this query
            return Transformations.map(mMiscDao.searchTags(query), tagsForQuery -> {
                return new Resource<List<Tag>>(alreasResource.status, tagsForQuery, alreasResource.message);
            });
        });
    }

    public LiveData<Resource<List<Tag>>> getTags(boolean mustFetch) {
        if (mFetchTagsLiveData != null) {
            if (mFetchTagsLiveData.getValue().status == Status.LOADING && !mustFetch) {
                // If the content is in loading state and the request doesn't require us to fetch again
                return mTagsPublicLiveData;
            }
            mTagsPublicLiveData.removeSource(mFetchTagsLiveData);
            mFetchTagsLiveData = null;
        }
        mFetchTagsLiveData = new NetworkBoundResource<List<Tag>, Response<ListTagsData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListTagsData> item) {
                db.beginTransaction();
                try {
                    mMiscDao.insertTags(item.getData().getTags());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Tag> data) {
                if (data == null || data.size() == 0) {
                    return true;
                }
                if (mustFetch) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Tag>> loadFromDb() {
                return mMiscDao.loadTags();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListTagsData>>> createCall() {
                return mApiService.getTags(new Request(""));
            }
        }.asLiveData();
        mTagsPublicLiveData.addSource(mFetchTagsLiveData, value -> {
            mTagsPublicLiveData.setValue(value);
        });
        return mTagsPublicLiveData;
    }
}
