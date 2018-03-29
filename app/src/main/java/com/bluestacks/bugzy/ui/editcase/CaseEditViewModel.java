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

import static com.bluestacks.bugzy.ui.editcase.CaseEditActivity.*;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import java.util.List;

import javax.inject.Inject;

public class CaseEditViewModel extends ViewModel {
    private Repository mRepository;
    private CasesRepository mCasesRepository;
    private MutableLiveData<Project> mCurrentProject = new MutableLiveData<>();
    private MutableLiveData<Pair<Integer, Integer>> mParamsLiveData = new MutableLiveData<>();
    private LiveData<Resource<List<Area>>> mAreas;
    private LiveData<Resource<Case>> mCaseLiveData;
    private LiveData<String> mToken;
    private LiveData<Resource<List<Milestone>>> mMilestones;

    @Inject
    CaseEditViewModel(Repository repository, CasesRepository casesRepository) {
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

        mAreas = Transformations.switchMap(mCurrentProject, val -> {
            return mRepository.getAreas(val.getId());
        });
        mMilestones = Transformations.switchMap(mCurrentProject, val -> {
            return mRepository.getMilestones(val.getId());
        });
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

    public LiveData<Resource<List<Milestone>>> getMilestones() {
        return mMilestones;
    }

    public LiveData<Resource<List<Area>>> getAreas() {
        return mAreas;
    }

    public LiveData<Resource<List<Project>>> getProjects() {
        return mRepository.getProjects(false);
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        return mRepository.getCategories(false);
    }

    public LiveData<Resource<List<Priority>>> getPriorities() {
        return mRepository.getPriorities(false);
    }

    public LiveData<Resource<List<CaseStatus>>> getStatuses() {
        return mRepository.getStatuses(false);
    }

    public LiveData<Resource<List<Person>>> getPeople() {
        return mRepository.getPeople(false);
    }

    public LiveData<String> getToken() {
        return mToken;
    }
}
