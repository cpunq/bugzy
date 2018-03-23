package com.bluestacks.bugzy.ui.editcase;


import com.bluestacks.bugzy.data.CasesRepository;
import com.bluestacks.bugzy.data.Repository;
import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.CaseStatus;
import com.bluestacks.bugzy.data.model.Category;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.data.model.Priority;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.data.model.Resource;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

public class CaseEditViewModel extends ViewModel {
    Repository mRepository;
    CasesRepository mCasesRepository;

    @Inject
    CaseEditViewModel(Repository repository, CasesRepository casesRepository) {
        mRepository = repository;
        mCasesRepository = casesRepository;
    }

    public LiveData<Resource<List<Milestone>>> getMilestones() {
        return mRepository.getMilestones(false);
    }

    public LiveData<Resource<List<Area>>> getAreas() {
        return mRepository.getAreas(false);
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
}
