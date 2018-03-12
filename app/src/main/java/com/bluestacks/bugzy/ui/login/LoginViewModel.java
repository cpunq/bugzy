package com.bluestacks.bugzy.ui.login;

import com.google.gson.Gson;

import com.bluestacks.bugzy.data.Repository;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.remote.model.Response;
import com.bluestacks.bugzy.data.remote.model.LoginData;
import com.bluestacks.bugzy.utils.SingleLiveEvent;
import com.bluestacks.bugzy.utils.Utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import javax.inject.Inject;


public class LoginViewModel extends ViewModel {
    private final SingleLiveEvent<String> mSnackBarText;
    private final MutableLiveData<String> mAccessTokenLiveData;
    private Repository mRepository;
    private MutableLiveData<Pair<String, String>> mCredentialsLiveData = new MutableLiveData<>();

    private LiveData<Resource<Response<LoginData>>> mLoginState;

    public LiveData<Resource<Response<LoginData>>> getLoginState() {
        return mLoginState;
    }

    private MediatorLiveData<Boolean> mIsLoggedIn = new MediatorLiveData<>();

    public MediatorLiveData<Boolean> getIsLoggedIn() {
        return mIsLoggedIn;
    }

    @Inject
    public LoginViewModel(Repository repository, Gson gson) {
        mRepository = repository;
        mSnackBarText = new SingleLiveEvent<>();
        mAccessTokenLiveData = new MutableLiveData<>();
        mCredentialsLiveData = new MutableLiveData<>();
        mIsLoggedIn = new MediatorLiveData<>();

        mIsLoggedIn.addSource(mRepository.getToken(), token -> {
            if (token == null) {
                mIsLoggedIn.setValue(false);
            } else {
                mIsLoggedIn.setValue(true);
            }
        });
        mLoginState = Transformations.switchMap(mCredentialsLiveData, pair -> {
            return mRepository.login(pair.first, pair.second);
        });
    }

    public MutableLiveData<Pair<String, String>> getCredentialsLiveData() {
        return mCredentialsLiveData;
    }

    protected SingleLiveEvent<String> getSnackBarText() {
        return mSnackBarText;
    }

    protected void onLoginButtonClicked(String email, String password) {
        if(!Utils.isValidInput(email)) {
            mSnackBarText.setValue("Please enter a correct email");
            return;
        }
        if(!Utils.isValidPassword(password)) {
            mSnackBarText.setValue("Invalid Password");
            return;
        }
        mCredentialsLiveData.setValue(new Pair<>(email, password));
    }
}
