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
import com.bluestacks.bugzy.data.model.Status;
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
    private boolean mDefaultSelectionsMade = false;
    private boolean mMergeInSelectionMade = false;
    private SingleLiveEvent<Integer> mUpdateRequiredMergeInSelection = new SingleLiveEvent<>();

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
    }

    @Inject
    CaseEditViewModel(Repository repository, CasesRepository casesRepository, AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
        mRepository = repository;
        mCasesRepository = casesRepository;

        mToken = mRepository.getToken();
        mAttachmentsLiveData.setValue(new ArrayList<>());
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
        preparePrimaryButtonText();
        mProjects = mRepository.getProjects(false);
        mAreas = Transformations.switchMap(mCurrentProject, val -> mRepository.getAreas(val.getId()));
        mMilestones = Transformations.switchMap(mCurrentProject, val -> mRepository.getMilestones(val.getId()));
        mCategories =  mRepository.getCategories(false);
        prepareStatuses();
        mEditCaseStatus = Transformations.switchMap(mEditCaseRequest, val -> mCasesRepository.editCase(val.first, val.second, mAttachmentsLiveData.getValue()));

        mPersons = mRepository.getPeople(false);
        mPriorities =  mRepository.getPriorities(false);
        prepareRequiredMergeIns();
        prepareDefaultPropSelection();
    }

    private void preparePrimaryButtonText() {
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
    }

    private void prepareStatuses() {
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
    }

    private void prepareRequiredMergeIns() {
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
                final List<String> list = new ArrayList<>();
                for (String entry : v) {
                    if (!TextUtils.isEmpty(entry)) {
                        list.add(entry);
                    }
                }
                if (caseStatus != null && caseStatus.data != null && !mMergeInSelectionMade) {
                    // Deferred means it is async
                    deferredUpdateRequiredMergeInSelection(caseStatus.data, list);
                    mMergeInSelectionMade = true;
                }
                return list;
            });

        });
    }

    private void prepareDefaultPropSelection() {
        // Assuming that mCaseLiveData is already being observed
        mDefaultPropSelectionLiveData.addSource(mCaseLiveData, caseResource -> {
            if (mParamsLiveData.getValue().first == MODE_ASSIGN) {
                mOpenPeopleSelector.call();
            }
            if (mParamsLiveData.getValue().first == MODE_NEW) {
                // For a new case, no need to do further stuff
                return;
            }
            if (caseResource.data != null && !mDefaultSelectionsMade) {
                prepareSelectionsFromCase(caseResource.data, mParamsLiveData.getValue().first);
                updateAllDefaultSelections();
            }
            if (caseResource.status == Status.SUCCESS && !mDefaultSelectionsMade) {
                prepareSelectionsFromCase(caseResource.data, mParamsLiveData.getValue().first);
                updateAllDefaultSelections();
                // Never update the selections from this point onwards, it might over-write user's selection
                mDefaultSelectionsMade = true;
            }
        });

        mDefaultPropSelectionLiveData.addSource(mProjects, v -> updateSelectionForPropType(PropType.PROJECT, v));
        mDefaultPropSelectionLiveData.addSource(mAreas, v -> updateSelectionForPropType(PropType.AREA, v));
        mDefaultPropSelectionLiveData.addSource(mMilestones, v -> updateSelectionForPropType(PropType.MILESTONE, v));
        mDefaultPropSelectionLiveData.addSource(mCategories, v -> updateSelectionForPropType(PropType.CATEGORY, v));
        mDefaultPropSelectionLiveData.addSource(mStatuses, v -> updateSelectionForPropType(PropType.STATUS, v));
        mDefaultPropSelectionLiveData.addSource(mPersons, v -> updateSelectionForPropType(PropType.ASSIGNEDTO, v));
        mDefaultPropSelectionLiveData.addSource(mPriorities, v -> updateSelectionForPropType(PropType.PRIORITY, v));
    }

    public <T> void updateSelectionForPropType(PropType propType, Resource<List<T>> data) {
        if (mCaseLiveData.getValue().data == null) {
            return;
        }
        if (data == null) {
            return;
        }
        if (data.data == null || data.data.size() == 0) {
            return;
        }
        mAppExecutors.diskIO().execute(() -> {
            if (!mUserPropSelection.containsKey(propType)) {
                return;
            }
            HashMap<PropType, Integer> map = new HashMap<>();
            try {
                map.put(propType, getIndex(mUserPropSelection.get(propType), data));
                if (propType == PropType.AREA) {
                }
                mAppExecutors.mainThread().execute(() -> mDefaultPropSelectionLiveData.setValue(map));
            } catch (IndexNotFoundException e) {
            }
        });
    }

    public void prepareSelectionsFromCase(Case caseDetails, int mode) {
        mUserPropSelection.put(PropType.PROJECT, caseDetails.getProjectId());
        mUserPropSelection.put(PropType.AREA, caseDetails.getProjectAreaId());
        mUserPropSelection.put(PropType.MILESTONE,caseDetails.getFixForId());
        mUserPropSelection.put(PropType.CATEGORY,caseDetails.getCategoryId());
        mUserPropSelection.put(PropType.STATUS,caseDetails.getStatusId());
        if (mode == MODE_RESOLVE) {
            mUserPropSelection.put(PropType.ASSIGNEDTO, caseDetails.getPersonOpenedById());
        } else {
            mUserPropSelection.put(PropType.ASSIGNEDTO, caseDetails.getPersonAssignedToId());
        }
        mUserPropSelection.put(PropType.PRIORITY, caseDetails.getPriority());
    }


    public SingleLiveEvent<Integer> getUpdateRequiredMergeInSelection() {
        return mUpdateRequiredMergeInSelection;
    }

    private void deferredUpdateRequiredMergeInSelection(final Case kase, List<String> list) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (kase == null) {
                    return;
                }
                int index = getRequiredMergeInPosition(kase.getRequiredMergeIn(), list);
                mUpdateRequiredMergeInSelection.postValue(index);
            }
        });
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


    void setParams(int mode, int caseId) {
        mDefaultSelectionsMade = false;
        mParamsLiveData.setValue(new Pair<>(mode, caseId));
    }

    public LiveData<Resource<Case>> getCaseLiveData() {
        return mCaseLiveData;
    }

    public void projectSelected(Project project, int position) {
        mCurrentProject.setValue(project);
            if (mCaseLiveData.getValue().data != null) {
            mUserPropSelection.put(PropType.PROJECT, getProjects().getValue().data.get(position).getId());
        }
    }

    public void categorySelected(Category cat, int pos) {
        mCurrentCategory.setValue(cat);
        if (mCaseLiveData.getValue().data != null) {
            mUserPropSelection.put(PropType.CATEGORY, getCategories().getValue().data.get(pos).getId());
        }
    }

    public void areaSelected(int position) {
        if (mCaseLiveData.getValue().data != null &&  mAreas.getValue().data != null) {
            mUserPropSelection.put(PropType.AREA, mAreas.getValue().data.get(position).getId());
        }
    }

    public void mileStoneSelected(int position) {
        if (mCaseLiveData.getValue().data != null && mMilestones.getValue().data != null) {
            mUserPropSelection.put(PropType.MILESTONE, mMilestones.getValue().data.get(position).getId());
        }
    }

    public void statusSelected(int p) {
        if (mCaseLiveData.getValue().data != null && getStatuses().getValue().data != null) {
            mUserPropSelection.put(PropType.STATUS, getStatuses().getValue().data.get(p).getId());
        }
    }

    public void assignedToSelected(int p) {
        if (mCaseLiveData.getValue().data != null && getPersons().getValue().data != null) {
            mUserPropSelection.put(PropType.ASSIGNEDTO, getPersons().getValue().data.get(p).getPersonid());
        }
    }

    public void prioritySelected(int p) {
        if (mCaseLiveData.getValue().data != null && getPriorities().getValue().data != null) {
            mUserPropSelection.put(PropType.PRIORITY, getPriorities().getValue().data.get(p).getId());
        }
    }

    @UiThread
    private void updateAllDefaultSelections() {
        // Offloading this work to a worker thread,
        // as it might be too much for the UI Thread
        HashMap<PropType, Integer> map = new HashMap<>();

        mAppExecutors.diskIO().execute(() -> {
            Resource<List<Area>> value = mAreas.getValue();
            if (value != null && value.data != null && value.data.size() > 0) {
                try {
                    map.put(PropType.AREA, getIndex(mUserPropSelection.get(PropType.AREA), value));
                } catch (IndexNotFoundException e) {
                }
            }
            if (getProjects().getValue() != null && getProjects().getValue().data != null && getProjects().getValue().data.size() > 0) {
                try {
                    map.put(PropType.PROJECT, getIndex(mUserPropSelection.get(PropType.PROJECT), getProjects().getValue()));
                } catch (IndexNotFoundException e) {
                }
            }
            if (getStatuses().getValue() != null && getStatuses().getValue().data != null && getStatuses().getValue().data.size() > 0) {
                try {
                    map.put(PropType.STATUS, getIndex(mUserPropSelection.get(PropType.STATUS), getStatuses().getValue()));
                } catch (IndexNotFoundException e) {
                }
            }

            if (getMilestones().getValue() != null && getMilestones().getValue().data != null && getMilestones().getValue().data.size() > 0) {
                try {
                    map.put(PropType.MILESTONE, getIndex(mUserPropSelection.get(PropType.MILESTONE), getMilestones().getValue()));
                } catch (IndexNotFoundException e) {
                }
            }
            if (getCategories().getValue() != null && getCategories().getValue().data != null && getCategories().getValue().data.size() > 0) {
                try {
                    map.put(PropType.CATEGORY, getIndex(mUserPropSelection.get(PropType.CATEGORY), getCategories().getValue()));
                } catch (IndexNotFoundException e) {
                }
            }
            if (getPersons().getValue() != null && getPersons().getValue().data != null && getPersons().getValue().data.size() > 0) {
                try {
                    map.put(PropType.ASSIGNEDTO, getIndex(mUserPropSelection.get(PropType.ASSIGNEDTO), getPersons().getValue()));
                } catch (IndexNotFoundException e) {
                }
            }
            if (getPriorities().getValue() != null && getPriorities().getValue().data != null && getPriorities().getValue().data.size() > 0) {
                try {
                    map.put(PropType.PRIORITY, getIndex(mUserPropSelection.get(PropType.PRIORITY), getPriorities().getValue()));
                } catch (IndexNotFoundException e) {
                }
            }
            mAppExecutors.mainThread().execute(() -> mDefaultPropSelectionLiveData.setValue(map));
        });
    }

    private int getRequiredMergeInPosition(String requiredMergeForCase,  List<String> requiredMergeIns) {
        List<String> branches = requiredMergeIns;
        if (branches == null || branches.size() == 0) {
            return 0;
        }
        for (int i = 0 ; i < branches.size() ; i ++) {
            if (branches.get(i).equals(requiredMergeForCase)) {
                return i;
            }
        }
        return 0;
    }

    public static class IndexNotFoundException extends Exception {
        IndexNotFoundException(String message){
            super(message);
        }
    }

    /**
     *
     * @param id - id of the object for which you wanna find the index
     * @param res - Resource of List of the Object type T
     * @param <T> - the Object type
     * @return - the index of the object with id = {id} in the data.getValue().data
     */
    @WorkerThread
    static <T> int getIndex(int id, Resource<List<T>> res) throws IndexNotFoundException{
        int i = 0;
        if (res == null) {
            return 0;
        }
        if (res.data == null) {
            return 0;
        }
        for (T t : res.data) {
            if (id == getId(t)) {
                return i;
            }
            i++;
        }
        throw new IndexNotFoundException("Index not found");
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

    public SingleLiveEvent<Void> getOpenPeopleSelector() {
        return mOpenPeopleSelector;
    }
}
