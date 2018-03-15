package com.bluestacks.bugzy.data;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.bluestacks.bugzy.data.local.db.BugzyDb;
import com.bluestacks.bugzy.data.local.db.MiscDao;
import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.Filter;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.model.SearchSuggestion;
import com.bluestacks.bugzy.data.remote.ApiResponse;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.data.remote.NetworkBoundResource;
import com.bluestacks.bugzy.data.remote.NetworkBoundTask;
import com.bluestacks.bugzy.data.remote.model.FiltersData;
import com.bluestacks.bugzy.data.remote.model.FiltersRequest;
import com.bluestacks.bugzy.data.remote.model.ListAreasData;
import com.bluestacks.bugzy.data.remote.model.ListMilestonesData;
import com.bluestacks.bugzy.data.remote.model.ListPeopleData;
import com.bluestacks.bugzy.data.remote.model.ListPeopleRequest;
import com.bluestacks.bugzy.data.remote.model.ListProjectsData;
import com.bluestacks.bugzy.data.remote.model.LoginData;
import com.bluestacks.bugzy.data.remote.model.LoginRequest;
import com.bluestacks.bugzy.data.remote.model.MyDetailsData;
import com.bluestacks.bugzy.data.remote.model.MyDetailsRequest;
import com.bluestacks.bugzy.data.remote.model.Request;
import com.bluestacks.bugzy.data.remote.model.Response;
import com.bluestacks.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class SearchSuggestionRepository {
    private final AppExecutors mAppExecutors;
    private FogbugzApiService mApiService;
    private Gson mGson;
    private MutableLiveData<String> mToken;
    private PrefsHelper mPrefs;
    private MiscDao mMiscDao;
    private BugzyDb db;

    public static final class SearchSuggestionType {
        public static final String MILESTONE ="milestone";
        public static final String AREA ="area";
        public static final String STATUS ="status";
        public static final String ASSIGNED_TO ="assigned_to";
        public static final String ORDER_BY ="order_by";
        public static final String PRIORITY ="priority";
    }


    @Inject
    SearchSuggestionRepository(AppExecutors appExecutors, FogbugzApiService apiService, Gson gson, PrefsHelper prefs, MiscDao miscDao, BugzyDb databaseObject) {
        mAppExecutors = appExecutors;
        mApiService = apiService;
        mGson = gson;
        mPrefs = prefs;
        mMiscDao = miscDao;
        db = databaseObject;
    }

    public void updateAreaSearchSuggestion(List<Area> areaList) {
        if (areaList == null || areaList.size() == 0) {
            return;
        }
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<SearchSuggestion> suggestions = new ArrayList<>();
                for (Area a : areaList) {
                    String text = "area:'" + a.getArea()+"'";
                    suggestions.add(new SearchSuggestion(text.replace("'",""), text, SearchSuggestionType.AREA));
                }
                mMiscDao.insertSearchSuggestions(suggestions);
            }
        });
    }

    public void updateMilestoneSearchSuggestion(List<Milestone> milestoneList) {
        if (milestoneList == null || milestoneList.size() == 0) {
            return;
        }
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<SearchSuggestion> suggestions = new ArrayList<>();
                for (Milestone m : milestoneList) {
                    String text = "milestone:'" + m.getName()+"'";
                    suggestions.add(new SearchSuggestion(text.replace("'",""), text, SearchSuggestionType.MILESTONE));
                }
                mMiscDao.insertSearchSuggestions(suggestions);
            }
        });

    }

    public void updatePeopleSearchSuggestion(List<Person> personList) {
        if (personList == null || personList.size() == 0) {
            return;
        }
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<SearchSuggestion> suggestions = new ArrayList<>();
                for (Person p : personList) {
                    String text = "assignedTo:'" + (p.getFullname() +"'");
                    String id = "assignedto:" + (p.getFullname());
                    suggestions.add(new SearchSuggestion(id, text, SearchSuggestionType.ASSIGNED_TO));
                }
                mMiscDao.insertSearchSuggestions(suggestions);
            }
        });
    }

    public LiveData<List<SearchSuggestion>> search(String q) {
        // Couldn't use Transformations because the sorting need to happen on a different thread
        MediatorLiveData<List<SearchSuggestion>> mMediator = new MediatorLiveData<>();

        String query = q.toLowerCase();
        query = query.replace("'","");
        query = query.replace("\"","");
        final String queryy = query;
        mMediator.addSource(mMiscDao.loadSearchSuggestions("%"+ query+"%"), value -> {
            mAppExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    Collections.sort(value, new Comparator<SearchSuggestion>() {
                        @Override
                        public int compare(SearchSuggestion searchSuggestion, SearchSuggestion t1) {
                            return Integer.compare(searchSuggestion.getId().indexOf(queryy), t1.getId().indexOf(queryy));
                        }
                    });
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            mMediator.setValue(value);
                        }
                    });
                }
            });
        });
        return mMediator;
    }
}
