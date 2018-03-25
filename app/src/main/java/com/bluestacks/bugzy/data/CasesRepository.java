package com.bluestacks.bugzy.data;


import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.bluestacks.bugzy.data.local.db.BugzyDb;
import com.bluestacks.bugzy.data.local.db.BugzyTypeConverters;
import com.bluestacks.bugzy.data.local.db.CaseDao;
import com.bluestacks.bugzy.data.model.SearchResultsResource;
import com.bluestacks.bugzy.data.remote.ApiResponse;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.data.remote.NetworkBoundResource;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.remote.model.Response;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.FilterCasesResult;
import com.bluestacks.bugzy.data.remote.model.ListCasesData;
import com.bluestacks.bugzy.data.remote.model.ListCasesRequest;
import com.bluestacks.bugzy.data.remote.model.SearchCasesRequest;
import com.bluestacks.bugzy.ui.search.AbsentLiveData;
import com.bluestacks.bugzy.utils.AppExecutors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.bluestacks.bugzy.data.CasesRepository.Sorting.AREA;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.AREA_R;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.CATEGORY;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.CATEGORY_R;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.MILESTONE;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.MILESTONE_R;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.PROJECT;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.PROJECT_R;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.PRIORITY;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.PRIORITY_R;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.STATUS;
import static com.bluestacks.bugzy.data.CasesRepository.Sorting.STATUS_R;

@Singleton
public class CasesRepository {
    private AppExecutors mAppExecutors;
    private FogbugzApiService mApiService;
    private PrefsHelper mPrefs;
    private BugzyDb db;
    private CaseDao mCaseDao;

    public static class Sorting {
        public static final String AREA = "Area";
        public static final String CATEGORY = "Category";
        public static final String MILESTONE= "Milestone";
        public static final String PROJECT  = "Project";
        public static final String PRIORITY = "Priority";
        public static final String STATUS   = "Status";

        public static final String AREA_R       = "Area (rev)";
        public static final String CATEGORY_R   = "Category (rev)";
        public static final String MILESTONE_R  = "Milestone (rev)";
        public static final String PROJECT_R    = "Project (rev)";
        public static final String PRIORITY_R   = "Priority (rev)";
        public static final String STATUS_R     = "Status (rev)";
    }


    @Inject
    CasesRepository(AppExecutors appExecutors, FogbugzApiService apiService, PrefsHelper prefs, CaseDao caseDao, BugzyDb database) {
        mAppExecutors = appExecutors;
        mApiService = apiService;
        mPrefs = prefs;
        mCaseDao = caseDao;
        db = database;
    }

    @WorkerThread
    public List<String> getSortingOrders(List<String> except) {
        HashSet<String> set = new HashSet<>();
        if (except != null) {
            set.addAll(except);
        }
        List<String> sortings = new ArrayList<>();
        // Only add if not to be excepted
        addIfNotToBeExcepted(sortings, set, AREA);
        addIfNotToBeExcepted(sortings, set, AREA_R);
        addIfNotToBeExcepted(sortings, set, CATEGORY);
        addIfNotToBeExcepted(sortings, set, CATEGORY_R);
        addIfNotToBeExcepted(sortings, set, MILESTONE);
        addIfNotToBeExcepted(sortings, set, MILESTONE_R);
        addIfNotToBeExcepted(sortings, set, PROJECT);
        addIfNotToBeExcepted(sortings, set, PROJECT_R);
        addIfNotToBeExcepted(sortings, set, PRIORITY);
        addIfNotToBeExcepted(sortings, set, PRIORITY_R);
        addIfNotToBeExcepted(sortings, set, STATUS);
        addIfNotToBeExcepted(sortings, set, STATUS_R);
        return sortings;
    }

    private void addIfNotToBeExcepted(List<String> list, HashSet<String> except, String entry) {
        if (!except.contains(entry)) {
            list.add(entry);
        }
    }


    @WorkerThread
    public void saveSortOrder(String filter, List<String> sortOrder) {
        db.beginTransaction();
        try {
            mCaseDao.updateSortOrders(filter, BugzyTypeConverters.fromList(sortOrder));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private LiveData<Resource<FilterCasesResult>> sorted(LiveData<Resource<FilterCasesResult>> inputLiveData) {
        return Transformations.switchMap(inputLiveData, filterData -> {
            MutableLiveData<Resource<FilterCasesResult>> sortedLiveData = new MutableLiveData<>();
            if (filterData == null) {
                sortedLiveData.setValue(null);
                return sortedLiveData;
            }
            if (filterData.data == null || filterData.data.getAppliedSortOrders() == null || filterData.data.getCases() == null) {
                if (filterData.data != null) {
                    // Set the availableSortOrders
                    filterData.data.setAvailableSortOrders(getSortingOrders(filterData.data.getAppliedSortOrders()));
                }
                sortedLiveData.setValue(filterData);
                return sortedLiveData;
            }

            // Good to go for sort
            mAppExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    filterData.data.setCases(sort(filterData.data.getCases(), filterData.data.getAppliedSortOrders()));
                    filterData.data.setAvailableSortOrders(getSortingOrders(filterData.data.getAppliedSortOrders()));
                    sortedLiveData.postValue(filterData);
                }
            });
            return sortedLiveData;
        });
    }

    @WorkerThread
    private List<Case> sort(List<Case> input, List<String> sortOrders) {
        List<Case> sorted = new ArrayList<>();
        sorted.addAll(input);

        for (int i = sortOrders.size()-1 ; i >= 0 ; i--) {
            final String order = sortOrders.get(i);
            Collections.sort(sorted, (a, b) -> {
                switch (order) {
                    case AREA:
                        return a.getProjectArea() != null ? a.getProjectArea().compareTo(b.getProjectArea()) : 0;
                    case AREA_R:
                        return b.getProjectArea() != null ? b.getProjectArea().compareTo(a.getProjectArea()) : 0;
                    case MILESTONE:
                        return a.getFixFor().compareTo(b.getFixFor());
                    case MILESTONE_R:
                        return b.getFixFor().compareTo(a.getFixFor());
                    case CATEGORY:
                        return a.getCategoryName() != null ? a.getCategoryName().compareTo(b.getCategoryName()) : 0;
                    case CATEGORY_R:
                        return b.getCategoryName() != null ? b.getCategoryName().compareTo(a.getCategoryName()) : 0;
                    case PRIORITY:
                        return Integer.compare(a.getPriority(), b.getPriority());
                    case PRIORITY_R:
                        return Integer.compare(b.getPriority(), a.getPriority());
                    case PROJECT:
                        return a.getProjectName().compareTo(b.getProjectName());
                    case PROJECT_R:
                        return b.getProjectName().compareTo(a.getProjectName());
                    case STATUS:
                        return a.getStatus().compareTo(b.getStatus());
                    case STATUS_R:
                        return b.getStatus().compareTo(a.getStatus());
                    default:
                        return 0;
                }
            });
        }
        return sorted;
    }

    public LiveData<Resource<FilterCasesResult>> cases(final String filter, boolean sortChanged) {
        return sorted(new NetworkBoundResource<FilterCasesResult, Response<ListCasesData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListCasesData> item) {
                db.beginTransaction();
                try {
                    mCaseDao.upsertCases(item.getData().getCases());
                    mCaseDao.upsertFilterCaseIds(new FilterCasesResult(filter, item.getData().getCaseIds(), null));
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable FilterCasesResult data) {
                if (sortChanged) {
                    // If the sort was changed, it means the cached data
                    // is requested again, and hence we won't do the
                    // network thing this time.
                    return false;
                }
                return true;
            }

            @NonNull
            @Override
            protected LiveData<FilterCasesResult> loadFromDb() {
                return Transformations.switchMap(mCaseDao.loadCasesForFilter(filter), filterCasesData -> {
                    if (filterCasesData == null) {
                        return AbsentLiveData.create();
                    }
                    return Transformations.map(mCaseDao.loadCasesById(filterCasesData.getCaseIds()), kases -> {
                        filterCasesData.setCases(kases);
                        return filterCasesData;
                    });
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListCasesData>>> createCall() {
                String[] cols =new String[]{
                        "sTitle","ixPriority","sStatus","sProject","sFixFor", "sPersonAssignedTo","sPersonOpenedBy", "sArea", "sCategory"
                };
                ListCasesRequest request = new ListCasesRequest(cols, filter);
                return mApiService.listCases(request);
            }
        }.asLiveData());
    }

    public LiveData<Resource<Case>> caseDetails(final Case kase) {
        return new NetworkBoundResource<Case, Response<ListCasesData>>(mAppExecutors) {
            @Override
            protected void saveCallResult(@NonNull Response<ListCasesData> item) {
                db.beginTransaction();
                try {
                    mCaseDao.upsert(item.getData().getCases().get(0));
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Case data) {
                // Fetch always
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Case> loadFromDb() {
                return mCaseDao.loadCaseById(kase.getIxBug());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListCasesData>>> createCall() {
                String[] cols =new String[]{
                        "sTitle","ixPriority","sStatus","sProject","sFixFor","sPersonAssignedTo", "sPersonOpenedBy", "sArea", "sCategory", "events", "requiredxmergexin"
                };
                return mApiService.searchCases(new SearchCasesRequest(cols, kase.getIxBug()+""));
            }
        }.asLiveData();
    }

    public LiveData<SearchResultsResource<List<Case>>> searchCases(final String query) {
        return Transformations.map(new NetworkBoundResource<List<Case>, Response<ListCasesData>>(mAppExecutors) {
            List<Case> mCases = null;

            @Override
            protected void saveCallResult(@NonNull Response<ListCasesData> item) {
                mCases = item.getData().getCases();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Case> data) {
                // Fetch always
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Case>> loadFromDb() {
                return new LiveData<List<Case>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(mCases);
                    }
                };
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListCasesData>>> createCall() {
                String[] cols =new String[]{
                        "sTitle","ixPriority","sStatus","sProject","sFixFor","sArea","sPersonAssignedTo","sPersonOpenedBy","events"
                };
                return mApiService.searchCases(new SearchCasesRequest(cols, query));
            }
        }.asLiveData(), v -> {
            return new SearchResultsResource<>(query, v);
        });
    }
}
