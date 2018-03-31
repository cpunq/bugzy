package com.bluestacks.bugzy.ui.editcase;


import com.bluestacks.bugzy.data.CasesRepository;
import com.bluestacks.bugzy.data.Repository;
import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.Attachment;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.CaseStatus;
import com.bluestacks.bugzy.data.model.Category;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.data.model.Priority;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.remote.model.CaseEditRequest;
import com.bluestacks.bugzy.data.remote.model.EditCaseData;
import com.bluestacks.bugzy.data.remote.model.Response;
import com.bluestacks.bugzy.ui.search.AbsentLiveData;
import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.utils.SingleLiveEvent;

import static com.bluestacks.bugzy.ui.editcase.CaseEditActivity.*;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class CaseEditViewModel extends ViewModel {
    private Repository mRepository;
    private CasesRepository mCasesRepository;
    private MutableLiveData<Project> mCurrentProject = new MutableLiveData<>();
    private MutableLiveData<Category> mCurrentCategory = new MutableLiveData<>();

    private MutableLiveData<Pair<Integer, Integer>> mParamsLiveData = new MutableLiveData<>();
    private LiveData<Resource<List<Area>>> mAreas;
    private LiveData<Resource<List<Milestone>>> mMilestones;

    private LiveData<Resource<List<Project>>> mProjects;
    private LiveData<Resource<List<CaseStatus>>> mStatuses;
    private LiveData<Resource<List<Category>>> mCategories;
    private LiveData<Resource<List<Person>>> mPersons;
    private LiveData<Resource<List<Priority>>> mPriorities;
    private LiveData<List<String>> mRequiredMergeIns;
    private SingleLiveEvent<Void> mOpenPeopleSelector = new SingleLiveEvent<>();
    private MediatorLiveData<String> mPrimaryButtonText = new MediatorLiveData<>();
    private MutableLiveData<String> mEventNote = new MutableLiveData<>();
    private MutableLiveData<Pair<CaseEditRequest, Integer>> mEditCaseRequest = new MutableLiveData<>();
    private LiveData<Resource<Response<EditCaseData>>> mEditCaseStatus;
    private MutableLiveData<List<Attachment>> mAttachmentsLiveData = new MutableLiveData<>();
    private SingleLiveEvent<Void> mScrollAttachmentsToLast = new SingleLiveEvent<>();

    private LiveData<Resource<Case>> mCaseLiveData;
    private LiveData<String> mToken;
    private HashMap<PropType, Integer> mUserPropSelection = new HashMap<>();
    private MediatorLiveData<HashMap<PropType, Integer>> mDefaultPropSelectionLiveData = new MediatorLiveData<>();
    private AppExecutors mAppExecutors;
    public enum PropType {
        PROJECT,
        AREA,
        MILESTONE,
        CATEGORY,
        STATUS,
        ASSIGNEDTO,
        PRIORITY,
        REQUIRED_MERGE_IN,
    }

    @Inject
    CaseEditViewModel(Repository repository, CasesRepository casesRepository, AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
        mRepository = repository;
        mCasesRepository = casesRepository;

        mToken = mRepository.getToken();

        mCaseLiveData = Transformations.switchMap(mToken, token -> {
            if (token == null) {
                return AbsentLiveData.create();
            }
            return Transformations.switchMap(mParamsLiveData, params -> {
                if (params.first != MODE_NEW) {
                    // Fetch case for param.second
                    Case kase = new Case();
                    kase.setIxBug(params.second);
                    return mCasesRepository.caseDetails(kase);
                }
                return AbsentLiveData.create();
            });
        });

        mPrimaryButtonText.addSource(mParamsLiveData, params -> {
            if (params.first == MODE_RESOLVE) {
                mPrimaryButtonText.setValue("Resolve");
                return;
            }
            if (params.first == MODE_ASSIGN) {
                mPrimaryButtonText.setValue("Assign");
                return;
            }
            if (params.first == MODE_EDIT) {
                mPrimaryButtonText.setValue("Save");
                return;
            }
            if (params.first == MODE_REOPEN) {
                mPrimaryButtonText.setValue("Reopen");
                return;
            }
            if (params.first == MODE_REACTIVATE) {
                mPrimaryButtonText.setValue("Reactivate");
                return;
            }
            if (params.first == MODE_CLOSE) {
                mPrimaryButtonText.setValue("Close");
                return;
            }
        });

        mAttachmentsLiveData.setValue(new ArrayList<>());

        // Assuming that mCaseLiveData is already being observed
        mDefaultPropSelectionLiveData.addSource(mCaseLiveData, caseResource -> {
            if (mParamsLiveData.getValue().first == MODE_ASSIGN) {
                mOpenPeopleSelector.call();
            }
            if (mParamsLiveData.getValue().first == MODE_NEW) {
                // For a new case, no need to do further stuff
                return;
            }
            // TODO: make sure, you disable the interactions, until this step completes
            updateDefaultSelections(caseResource.data, mParamsLiveData.getValue().first);
        });

        mAreas = Transformations.switchMap(mCurrentProject, val -> {
            return mRepository.getAreas(val.getId());
        });
        mMilestones = Transformations.switchMap(mCurrentProject, val -> {
            return mRepository.getMilestones(val.getId());
        });
        mStatuses = Transformations.switchMap(mCurrentCategory, val -> {
            return Transformations.map(mRepository.getStatuses(val.getId()), v -> {
                if (v.data == null) {
                    return v;
                }
                if (mParamsLiveData.getValue().first == MODE_RESOLVE) {
                    List<CaseStatus> statuses = new ArrayList<>();
                    for (CaseStatus status : v.data) {
                        // Only keep non-active statuses
                        if (!status.getName().toLowerCase().contains("active")) {
                            statuses.add(status);
                        }
                    }
                    return new Resource<>(v.status, statuses, v.message);
                }
                if (mParamsLiveData.getValue().first == MODE_REOPEN || mParamsLiveData.getValue().first == MODE_REACTIVATE) {
                    List<CaseStatus> statuses = new ArrayList<>();
                    for (CaseStatus status : v.data) {
                        if (status.getName().toLowerCase().contains("active")) {
                            statuses.add(status);
                            return new Resource<>(v.status, statuses, v.message);
                        }
                    }
                }
                return v;
            });
        });
        mEditCaseStatus = Transformations.switchMap(mEditCaseRequest, val -> {
            return mCasesRepository.editCase(val.first, val.second);
        });

        mRequiredMergeIns = Transformations.switchMap(mCaseLiveData, caseStatus -> {
            return Transformations.map(mCasesRepository.getRequiredMergeIns(), v -> {
                // If we don't get from the db
                // we get it generate it from the current case
                if (v == null || v.size() == 0) {
                    List<String> l =new ArrayList<>();
                    if (caseStatus != null && caseStatus.data != null && !TextUtils.isEmpty(caseStatus.data.getRequiredMergeIn())) {
                        l.add(caseStatus.data.getRequiredMergeIn());
                    }
                    return l;
                }
                List<String> list = new ArrayList<>();
                for (String entry : v) {
                    if (!TextUtils.isEmpty(entry)) {
                        list.add(entry);
                    }
                }
                return list;
            });

        });

        mProjects = mRepository.getProjects(false);
        mCategories =  mRepository.getCategories(false);
        mPriorities =  mRepository.getPriorities(false);
        mPersons = mRepository.getPeople(false);
    }

    public void addAttachment(Uri fileUri) {
        Attachment at = new Attachment();
        at.setUri(fileUri);
        mAttachmentsLiveData.getValue().add(at);
        // Trigger update
        mAttachmentsLiveData.setValue(mAttachmentsLiveData.getValue());
        mScrollAttachmentsToLast.call();
    }

    public void removeAttachment(int index) {
        mAttachmentsLiveData.getValue().remove(index);
        // Trigger update
        mAttachmentsLiveData.setValue(mAttachmentsLiveData.getValue());


    }

    public MutableLiveData<List<Attachment>> getAttachmentsLiveData() {
        return mAttachmentsLiveData;
    }

    public LiveData<List<String>> getRequiredMergeIns() {
        return mRequiredMergeIns;
    }

    public LiveData<Resource<Response<EditCaseData>>> getEditCaseStatus() {
        return mEditCaseStatus;
    }

    public MediatorLiveData<String> getPrimaryButtonText() {
        return mPrimaryButtonText;
    }

    void setParams(int mode, int caseId) {
        mParamsLiveData.setValue(new Pair<>(mode, caseId));
    }

    public LiveData<Resource<Case>> getCaseLiveData() {
        return mCaseLiveData;
    }

    public void projectSelected(Project project) {
        mCurrentProject.setValue(project);
    }

    public void categorySelected(Category cat) {
        mCurrentCategory.setValue(cat);
    }

    public LiveData<Resource<List<Area>>> getAreas() {
        return mAreas;
    }

    public LiveData<Resource<List<Milestone>>> getMilestones() {
        return mMilestones;
    }

    public LiveData<Resource<List<Project>>> getProjects() {
        return mProjects;
    }

    public LiveData<Resource<List<CaseStatus>>> getStatuses() {
        return mStatuses;
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        return mCategories;
    }

    public LiveData<Resource<List<Person>>> getPersons() {
        return mPersons;
    }

    public LiveData<Resource<List<Priority>>> getPriorities() {
        return mPriorities;
    }

    public LiveData<String> getToken() {
        return mToken;
    }

    public MediatorLiveData<HashMap<PropType, Integer>> getDefaultPropSelectionLiveData() {
        return mDefaultPropSelectionLiveData;
    }

    public SingleLiveEvent<Void> getScrollAttachmentsToLast() {
        return mScrollAttachmentsToLast;
    }

    @UiThread
    private void updateDefaultSelections(Case kase, int mode) {
        if (kase == null) {
            return;
        }
        // Offloading this work to a worker thread,
        // as it might be too much for the UI Thread
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                HashMap<PropType, Integer> map = new HashMap<>();
                map.put(PropType.AREA, getIndex(kase.getProjectAreaId(), getAreas()));
                map.put(PropType.PROJECT, getIndex(kase.getProjectId(), getProjects()));
                map.put(PropType.MILESTONE, getIndex(kase.getFixForId(), getMilestones()));
                map.put(PropType.CATEGORY, getIndex(kase.getCategoryId(), getCategories()));
                map.put(PropType.STATUS, getIndex(kase.getStatusId(), getStatuses()));
                if (mode == MODE_RESOLVE) {
                    map.put(PropType.ASSIGNEDTO, getIndex(kase.getPersonOpenedById(), getPersons()));
                } else {
                    map.put(PropType.ASSIGNEDTO, getIndex(kase.getPersonAssignedToId(), getPersons()));
                }
                map.put(PropType.PRIORITY, getIndex(kase.getPriority(), getPriorities()));
                map.put(PropType.REQUIRED_MERGE_IN, getIndex(kase.getPriority(), getPriorities()));
                mDefaultPropSelectionLiveData.postValue(map);
            }
        });
    }

    public SingleLiveEvent<Void> getOpenPeopleSelector() {
        return mOpenPeopleSelector;
    }

    /**
     *
     * @param id - id of the object for which you wanna find the index
     * @param data - LiveData of the Resource of List of the Object type T
     * @param <T> - the Object type
     * @return - the index of the object with id = {id} in the data.getValue().data
     */
    @WorkerThread
    static <T> int getIndex(int id, LiveData<Resource<List<T>>> data) {
        int i = 0;
        if (data.getValue() == null) {
            return 0;
        }
        if (data.getValue().data == null) {
            return 0;
        }
        for (T t : data.getValue().data) {
            if (id == getId(t)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    static int getId(Object o) {
        if (o instanceof Milestone) {
            return ((Milestone) o).getId();
        }
        if (o instanceof Project) {
            return ((Project) o).getId();
        }
        if (o instanceof Area) {
            return ((Area) o).getId();
        }
        if (o instanceof Category) {
            return ((Category) o).getId();
        }
        if (o instanceof CaseStatus) {
            return ((CaseStatus) o).getId();
        }
        if (o instanceof Person) {
            return ((Person) o).getPersonid();
        }
        if (o instanceof Priority) {
            return ((Priority) o).getId();
        }
        return -1;
    }

    void saveClicked(String title, Project project, Area area, Milestone milestone, Category category, CaseStatus caseStatus,
                                                           Person p, Priority priority, String tags, String foundIn, String fixedIn,
                                                           String verifiedIn, String eventContent, String requiredMergeIn) {
        int mode = mParamsLiveData.getValue().first;

        CaseEditRequest request = new CaseEditRequest();
        if (mode != MODE_NEW) {
            request.setBugId(mCaseLiveData.getValue().data.getIxBug());
        }
        if (mode != MODE_CLOSE) {
            request.setTitle(title);
            request.setProjectId(project.getId());
            request.setProjectAreaId(area.getId());
            request.setFixForId(milestone.getId());
            request.setCategoryId(category.getId());
            request.setStatusId(caseStatus.getId());
            request.setPersonAssignedToId(p.getPersonid());
            request.setPriority(priority.getId());
            request.setTags(Arrays.asList(tags.split(", ")));
        }

        request.setRequiredMergeIn(requiredMergeIn);
        request.setFoundIn(foundIn);
        request.setFixedIn(fixedIn);
        request.setVerifiedIn(verifiedIn);
        request.setEventText(eventContent);
        mEditCaseRequest.setValue(new Pair(request, mode));
    }
}
