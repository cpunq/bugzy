package com.bluestacks.bugzy.ui.casedetails;


import com.bluestacks.bugzy.data.CasesRepository;
import com.bluestacks.bugzy.data.Repository;
import com.bluestacks.bugzy.models.Resource;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.utils.SingleLiveEvent;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

public class CaseDetailsFragmentViewModel extends ViewModel {
    private CasesRepository mCasesRepository;
    private Repository mRepository;

    private MutableLiveData<Case> mCase = new MutableLiveData<>();
    private SingleLiveEvent<String> mSnackBarText =  new SingleLiveEvent<>();
    private MediatorLiveData<String> mToken = new MediatorLiveData<>();
    private LiveData<Resource<Case>> mCaseState;

    @Inject
    CaseDetailsFragmentViewModel(CasesRepository casesRepository, Repository repository) {
        mCasesRepository = casesRepository;
        mRepository = repository;

        LiveData<Resource<Case>> liveData = Transformations.switchMap(mCase, kase -> mCasesRepository.caseDetails(kase));

        mCaseState = Transformations.switchMap(liveData, caseState -> {
            if (caseState.status == Status.SUCCESS && caseState.data == null) {
                mSnackBarText.setValue("Failed to get case details");
            } else if (caseState.status == Status.ERROR) {
                mSnackBarText.setValue(caseState.message);
            }

            return new LiveData<Resource<Case>>() {
                @Override
                protected void onActive() {
                    super.onActive();
                    setValue(caseState);
                }
            };
        });

        mToken.addSource(mRepository.getToken(), token -> {
            mToken.setValue(token);
        });
    }

    public void loadCaseDetails(Case kase) {
        mCase.setValue(kase);
    }

    public LiveData<Resource<Case>> getCaseState() {
        return mCaseState;
    }

    public SingleLiveEvent<String> getSnackBarText() {
        return mSnackBarText;
    }

    public MediatorLiveData<String> getToken() {
        return mToken;
    }
}
