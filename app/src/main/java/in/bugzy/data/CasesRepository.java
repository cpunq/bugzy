package in.bugzy.data;


import com.google.gson.Gson;

import in.bugzy.data.local.PrefsHelper;
import in.bugzy.data.local.db.BugzyDb;
import in.bugzy.data.local.db.BugzyTypeConverters;
import in.bugzy.data.local.db.CaseDao;
import in.bugzy.data.local.db.MiscDao;
import in.bugzy.data.model.Area;
import in.bugzy.data.model.Attachment;
import in.bugzy.data.model.Milestone;
import in.bugzy.data.model.Project;
import in.bugzy.data.model.RecentSearch;
import in.bugzy.data.model.SearchResultsResource;
import in.bugzy.data.remote.ApiResponse;
import in.bugzy.data.remote.FogbugzApiService;
import in.bugzy.data.remote.NetworkBoundResource;
import in.bugzy.data.model.Resource;
import in.bugzy.data.remote.NetworkBoundTask;
import in.bugzy.data.remote.model.CaseEditRequest;
import in.bugzy.data.remote.model.EditCaseData;
import in.bugzy.data.remote.model.Response;
import in.bugzy.data.model.Case;
import in.bugzy.data.model.FilterCasesResult;
import in.bugzy.data.remote.model.ListCasesData;
import in.bugzy.data.remote.model.ListCasesRequest;
import in.bugzy.data.remote.model.SearchCasesRequest;
import in.bugzy.ui.editcase.CaseEditActivity;
import in.bugzy.ui.search.AbsentLiveData;
import in.bugzy.utils.AppExecutors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import static in.bugzy.data.CasesRepository.Sorting.AREA;
import static in.bugzy.data.CasesRepository.Sorting.AREA_R;
import static in.bugzy.data.CasesRepository.Sorting.CATEGORY;
import static in.bugzy.data.CasesRepository.Sorting.CATEGORY_R;
import static in.bugzy.data.CasesRepository.Sorting.MILESTONE;
import static in.bugzy.data.CasesRepository.Sorting.MILESTONE_R;
import static in.bugzy.data.CasesRepository.Sorting.PROJECT;
import static in.bugzy.data.CasesRepository.Sorting.PROJECT_R;
import static in.bugzy.data.CasesRepository.Sorting.PRIORITY;
import static in.bugzy.data.CasesRepository.Sorting.PRIORITY_R;
import static in.bugzy.data.CasesRepository.Sorting.STATUS;
import static in.bugzy.data.CasesRepository.Sorting.STATUS_R;

@Singleton
public class CasesRepository {
    private AppExecutors mAppExecutors;
    private FogbugzApiService mApiService;
    private PrefsHelper mPrefs;
    private BugzyDb db;
    private CaseDao mCaseDao;
    private MiscDao mMiscDao;
    private Gson mGson;

    private String[] mColsForCaseList =new String[]{
            "sTitle",
            "ixPriority",
            "sStatus",
            "ixStatus",
            "sProject",
            "ixProject",
            "sFixFor",
            "ixFixFor",
            "sPersonAssignedTo",
            "ixPersonAssignedTo",
            "sPersonOpenedBy",
            "ixPersonOpenedBy",
            "sArea",
            "ixArea",
            "sCategory",
            "ixCategory",
            "dtLastUpdated"
};

    private String[] mColsForCaseDetails =new String[]{
            "sTitle",
            "ixPriority",
            "sStatus",
            "ixStatus",
            "sProject",
            "ixProject",
            "sFixFor",
            "ixFixFor",
            "sPersonAssignedTo",
            "ixPersonAssignedTo",
            "sPersonOpenedBy",
            "ixPersonOpenedBy",
            "sArea",
            "ixArea",
            "sCategory",
            "ixCategory",
            "dtLastUpdated",

            // New cols
            "events",
            "requiredxmergexin",
            "tags",
            "fixedxin",
            "productxversion",
            "verifiedxin"
    };

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
    CasesRepository(AppExecutors appExecutors, FogbugzApiService apiService, PrefsHelper prefs, CaseDao caseDao, BugzyDb database, MiscDao miscDao, Gson gson) {
        mAppExecutors = appExecutors;
        mApiService = apiService;
        mPrefs = prefs;
        mCaseDao = caseDao;
        mMiscDao = miscDao;
        db = database;
        mGson = gson;
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
                        Collections.sort(kases, new Comparator<Case>() {
                            @Override
                            public int compare(Case aCase, Case t1) {
                                return Long.compare(t1.getLastUpdatedAt().getTime(), aCase.getLastUpdatedAt().getTime());
                            }
                        });
                        filterCasesData.setCases(kases);
                        return filterCasesData;
                    });
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Response<ListCasesData>>> createCall() {
                ListCasesRequest request = new ListCasesRequest(mColsForCaseList, filter);
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
                    // Saving project, area and milestone again in db, because they might be deleted
                    Case caseDetails = item.getData().getCases().get(0);
                    mMiscDao.insertProjects(Collections.singletonList(Project.createfromCase(caseDetails)));
                    mMiscDao.insert(Collections.singletonList(Area.createfromCase(caseDetails)));
                    mMiscDao.insertMilestones(Collections.singletonList(Milestone.createfromCase(caseDetails)));

                    mCaseDao.upsert(caseDetails);
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
                return mApiService.searchCases(new SearchCasesRequest(mColsForCaseDetails, kase.getIxBug()+""));
            }
        }.asLiveData();
    }

    public LiveData<List<RecentSearch>> getRecentSearches() {
        return mMiscDao.loadRecentSearches();
    }

    public void recordResearch(String query) {
        if (query == null || query.trim().equals("")) {
            return;
        }
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    db.beginTransaction();
                    mMiscDao.insert(new RecentSearch(query));
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        });
    }

    public LiveData<SearchResultsResource<ListCasesData>> searchCases(final String query) {
        this.recordResearch(query);
        NetworkBoundTask<Response<ListCasesData>> task = new NetworkBoundTask<Response<ListCasesData>>(mAppExecutors, mGson) {
            @Override
            public void saveCallResult(@NonNull Response<ListCasesData> result) {
            }

            @NonNull
            @Override
            protected Call<Response<ListCasesData>> createCall() {
                return mApiService.searchCasesCall(new SearchCasesRequest(mColsForCaseList, query));
            }
        };
        mAppExecutors.networkIO().execute(task);
        return Transformations.map(task.asLiveData(), v -> {
            Resource<ListCasesData> resource = new Resource(v.status, v.data == null ? null : v.data.getData(), v.message);
            return new SearchResultsResource<>(query, resource);
        });
//        return Transformations.map(new NetworkBoundResource<List<Case>, Response<ListCasesData>>(mAppExecutors) {
//            List<Case> mCases = null;
//
//            @Override
//            protected void saveCallResult(@NonNull Response<ListCasesData> item) {
//                mCases = item.getData().getCases();
//            }
//
//            @Override
//            protected boolean shouldFetch(@Nullable List<Case> data) {
//                // Fetch always
//                return true;
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<List<Case>> loadFromDb() {
//                return new LiveData<List<Case>>() {
//                    @Override
//                    protected void onActive() {
//                        super.onActive();
//                        setValue(mCases);
//                    }
//                };
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<ApiResponse<Response<ListCasesData>>> createCall() {
//                return mApiService.searchCases(new SearchCasesRequest(mColsForCaseList, query));
//            }
//        }.asLiveData(), v -> {
//            return new SearchResultsResource<>(query, v);
//        });
    }

    public LiveData<Resource<Response<EditCaseData>>> editCase(final CaseEditRequest request, int mode, List<Attachment> attachments) {
        NetworkBoundTask<Response<EditCaseData>> task =  new NetworkBoundTask<Response<EditCaseData>>(mAppExecutors, mGson) {
            @Override
            public void saveCallResult(@NonNull Response<EditCaseData> result) {
                // Save?? ;)
                if (result.getData() == null) {
                    return;
                }
            }

            @NonNull
            @Override
            protected Call<Response<EditCaseData>> createCall() {
                List<MultipartBody.Part> fileParts = new ArrayList<>();
                int i = 0;
                for (Attachment attachment : attachments) {
                    fileParts.add(prepareFilePart("File" + i, attachment.getUri()));
                    i++;
                }
                request.setFileCount(fileParts.size());
                request.setToken(mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN));

                switch (mode) {
                    case CaseEditActivity.MODE_EDIT:
                    case CaseEditActivity.MODE_ASSIGN:
                       return mApiService.editCase(request, fileParts);
                    case CaseEditActivity.MODE_NEW:
                        return mApiService.newCase(request, fileParts);
                    case CaseEditActivity.MODE_CLOSE:
                        return mApiService.closeCase(request, fileParts);
                    case CaseEditActivity.MODE_RESOLVE:
                        return mApiService.resolveCase(request, fileParts);
                    case CaseEditActivity.MODE_REACTIVATE:
                        return mApiService.reactivateCase(request, fileParts);
                    case CaseEditActivity.MODE_REOPEN:
                        return mApiService.reopenCase(request, fileParts);
                    default:
                        return mApiService.editCase(request, fileParts);
                }
            }
        };
        mAppExecutors.networkIO().execute(task);
        return task.asLiveData();
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath());

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getMimeType(fileUri.getPath())),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public LiveData<List<String>> getRequiredMergeIns() {
        return mCaseDao.getRequiredMergeIns();
    }
}
