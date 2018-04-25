package in.bugzy.data;


import com.google.gson.Gson;

import in.bugzy.data.local.PrefsHelper;
import in.bugzy.data.local.db.BugzyDb;
import in.bugzy.data.local.db.MiscDao;
import in.bugzy.data.model.Area;
import in.bugzy.data.model.Milestone;
import in.bugzy.data.model.Person;
import in.bugzy.data.model.SearchSuggestion;
import in.bugzy.data.remote.FogbugzApiService;
import in.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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
        public static final String OPENED_BY ="opened_by";
        public static final String ORDER_BY ="order_by";
        public static final String PRIORITY ="priority";
        public static final String LAST_EDITED ="last_edited";
        public static final String OPENED ="opened";
        public static final String CLOSED ="closed";
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

    @WorkerThread
    public void insertDefaultSearchSuggestions() {
        List<SearchSuggestion> suggestions = new ArrayList<>();

        List<String> orderingOptions = getOrderringOptions();
        for (String option : orderingOptions) {
            String text = "orderBy:"+option;
            String id = "orderby:"+option;
            suggestions.add(new SearchSuggestion(id, text, SearchSuggestionType.ORDER_BY));
        }

        for (int i = 1 ; i < 8 ; i++) {
            String text = "priority:"+i;
            suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.PRIORITY));
        }

        for (String option : getLastEditOptions()) {
            String text = "lastEdited:" + option;
            String id = "lastedited:" + option.replace("'", "");
            suggestions.add(new SearchSuggestion(id, text, SearchSuggestionType.LAST_EDITED));
        }
        for (String option : getLastEditOptions()) {
            String text = "opened:" + option;
            String id = "opened:" + option.replace("'", "");
            suggestions.add(new SearchSuggestion(id, text, SearchSuggestionType.OPENED));
        }
        for (String option : getLastEditOptions()) {
            String text = "closed:" + option;
            String id = "closed:" + option.replace("'", "");
            suggestions.add(new SearchSuggestion(id, text, SearchSuggestionType.CLOSED));
        }

        String text = "status:active";
        suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.STATUS));
        text = "status:closed";
        suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.STATUS));
        text = "status:open";
        suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.STATUS));
        text = "status:resolved";
        suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.STATUS));

        db.miscDao().insertSearchSuggestions(suggestions);
    }

    private List<String> getOrderringOptions() {
        List<String> options = new ArrayList<>();
        options.add("area");
        options.add("priority");
        options.add("milestone");
        options.add("category");
        options.add("status");
        options.add("lastEdited");
        return options;
    }

    private List<String> getLastEditOptions() {
        List<String> options = new ArrayList<>();
        options.add("today");
        options.add("yesterday");
        options.add("'last month'");
        options.add("'last week'");
        options.add("'this month'");
        options.add("'this week'");
        return options;
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

                    text = "openedBy:'" + (p.getFullname() +"'");
                    id = "openedby:" + (p.getFullname());
                    suggestions.add(new SearchSuggestion(id, text, SearchSuggestionType.OPENED_BY));
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
                    List<SearchSuggestion> trimmed = value.subList(0,Math.min(value.size(), 30));
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            mMediator.setValue(trimmed);
                        }
                    });
                }
            });
        });
        return mMediator;
    }
}
