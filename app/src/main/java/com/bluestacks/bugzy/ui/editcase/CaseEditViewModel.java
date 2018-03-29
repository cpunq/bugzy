package com.bluestacks.bugzy.ui.editcase;


import com.bluestacks.bugzy.data.CasesRepository;
import com.bluestacks.bugzy.data.Repository;
import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.CaseStatus;
import com.bluestacks.bugzy.data.model.Category;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.data.model.Priority;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.ui.search.AbsentLiveData;
import com.bluestacks.bugzy.utils.AppExecutors;

import static com.bluestacks.bugzy.ui.editcase.CaseEditActivity.*;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Pair;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class CaseEditViewModel extends ViewModel {
    private Repository mRepository;
    private CasesRepository mCasesRepository;
    private MutableLiveData<Project> mCurrentProject = new MutableLiveData<>();
    private MutableLiveData<Pair<Integer, Integer>> mParamsLiveData = new MutableLiveData<>();
    private LiveData<Resource<List<Area>>> mAreas;
    private LiveData<Resource<List<Milestone>>> mMilestones;

    private LiveData<Resource<List<Project>>> mProjects;
    private LiveData<Resource<List<CaseStatus>>> mStatuses;
    private LiveData<Resource<List<Category>>> mCategories;
    private LiveData<Resource<List<Person>>> mPersons;
    private LiveData<Resource<List<Priority>>> mPriorities;


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
        PRIORITY
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

        // Assuming that mCaseLiveData is already being observed
        mDefaultPropSelectionLiveData.addSource(mCaseLiveData, caseResource -> {
            // TODO: make sure, you disable the interactions, until this step completes
            updateDefaultSelections(caseResource.data);
        });

        mAreas = Transformations.switchMap(mCurrentProject, val -> {
            return mRepository.getAreas(val.getId());
        });
        mMilestones = Transformations.switchMap(mCurrentProject, val -> {
            return mRepository.getMilestones(val.getId());
        });

        mProjects = mRepository.getProjects(false);
        mCategories =  mRepository.getCategories(false);
        mPriorities =  mRepository.getPriorities(false);
        mStatuses = mRepository.getStatuses(false);
        mPersons = mRepository.getPeople(false);
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

    @UiThread
    private void updateDefaultSelections(Case kase) {
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
                map.put(PropType.ASSIGNEDTO, getIndex(kase.getPersonAssignedToId(), getPersons()));
                map.put(PropType.PRIORITY, getIndex(kase.getPriority(), getPriorities()));
                mDefaultPropSelectionLiveData.postValue(map);
            }
        });
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
}
