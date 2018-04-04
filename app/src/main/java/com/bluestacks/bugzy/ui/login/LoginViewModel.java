package com.bluestacks.bugzy.ui.login;

import com.bluestacks.bugzy.BugzyApp;
import com.bluestacks.bugzy.data.Repository;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.data.remote.model.Response;
import com.bluestacks.bugzy.data.remote.model.LoginData;
import com.bluestacks.bugzy.utils.SingleLiveEvent;
import com.bluestacks.bugzy.utils.SnackbarMessage;
import com.bluestacks.bugzy.utils.Utils;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;

import javax.inject.Inject;


public class LoginViewModel extends ViewModel {
    public enum LoginStep {
        ORG, CREDENTIALS, THEME, INFO
    }
    private final SnackbarMessage<String> mSnackBarText;
    private final MutableLiveData<String> mAccessTokenLiveData;
    private Repository mRepository;
    private MutableLiveData<Pair<String, String>> mCredentialsLiveData = new MutableLiveData<>();
    private MutableLiveData<LoginStep> mLoginStepLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mFetchOrganisationLogoCommand = new MutableLiveData<>();
    private MediatorLiveData<String> mNextButtonText = new MediatorLiveData<>();
    private LiveData<Resource<String>> mOrganisationLogoResource;
    private LiveData<Resource<Response<LoginData>>> mLoginState;
    private MediatorLiveData<Boolean> mIsLoggedIn = new MediatorLiveData<>();
    private MutableLiveData<String> mUrlMessage = new MutableLiveData<>();
    private SingleLiveEvent<Void> mHomeScreenCommand = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> mChangeThemeCommand = new SingleLiveEvent<>();
    private BugzyApp mApp;
    private String mPassword;
    private String mEmail;
    private String mOrganisation;
    private Handler mHandler;

    public LiveData<Resource<Response<LoginData>>> getLoginState() {
        return mLoginState;
    }

    public MediatorLiveData<Boolean> getIsLoggedIn() {
        return mIsLoggedIn;
    }

    @Inject
    public LoginViewModel(Repository repository, Application application) {
        mApp = (BugzyApp) application;
        mRepository = repository;
        mSnackBarText = new SnackbarMessage<>();
        mAccessTokenLiveData = new MutableLiveData<>();
        mCredentialsLiveData = new MutableLiveData<>();
        mIsLoggedIn = new MediatorLiveData<>();
        mHandler = new Handler();

        mIsLoggedIn.addSource(mRepository.getToken(), token -> {
            if (token == null) {
                mIsLoggedIn.setValue(false);
            } else {
                mIsLoggedIn.setValue(true);
//                mLoginStepLiveData.setValue(LoginStep.THEME);
            }
        });

        mLoginState = Transformations.switchMap(mCredentialsLiveData, pair -> {
            return Transformations.map(mRepository.login(pair.first, pair.second, mOrganisation), v -> {
                if (v.status == Status.SUCCESS) {
                    // If login success, go to theme step
                    mLoginStepLiveData.setValue(LoginStep.THEME);

                    if (v.status == Status.SUCCESS) {
                        mSnackBarText.setValue("Logged In");
                    }
                }
                return v;
            });
        });

        // OrganisationLogoResource is dependent on the fetchLogoCommand
        mOrganisationLogoResource = Transformations.switchMap(mFetchOrganisationLogoCommand, organisationName -> {
            return Transformations.map(mRepository.getCompanyLogo(organisationName), resourceState -> {
                if (resourceState.status == Status.SUCCESS) {
                    String logo =  resourceState.data.size() > 0 ? resourceState.data.get(0).getLogo() : "";
                    return Resource.success(logo);
                }
                return new Resource<String>(resourceState.status, "", resourceState.message);
            });
        });

        mNextButtonText.addSource(mLoginStepLiveData, step -> {
            switch (step) {
                case ORG:
                    mNextButtonText.setValue("Next");
                    break;
                case CREDENTIALS:
                    mNextButtonText.setValue("Login");
                    break;
                case THEME:
                    mNextButtonText.setValue("Next");
                    break;
                case INFO:
                    mNextButtonText.setValue("Show me the Bugz");
                    break;
            }
        });

        mLoginStepLiveData.setValue(LoginStep.ORG);
    }

    public void organisationNameChanged(String organisationName) {
        mOrganisation = organisationName;
        mUrlMessage.setValue("We will make a connection at https://"+mOrganisation.toLowerCase()+".manuscript.com");
        // Removing existing callbacks
        mHandler.removeCallbacksAndMessages(null);
        // Waiting for 2 seconds before calling the logo api
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(organisationName)) {
                    return;
                }
                if (organisationName.length() < 3) {
                    return;
                }
                mFetchOrganisationLogoCommand.setValue(organisationName);
            }
        }, 1000);
    }

    public void nextClicked() {
        switch (mLoginStepLiveData.getValue()) {
            case ORG:
                if (!TextUtils.isEmpty(mOrganisation)) {
                    mLoginStepLiveData.setValue(LoginStep.CREDENTIALS);
                } else {
                    mSnackBarText.setValue("Please enter a valid organisation");
                }
                break;
            case CREDENTIALS:
                login();
                break;
            case THEME:
                mLoginStepLiveData.setValue(LoginStep.INFO);
                break;
            case INFO:
                mHomeScreenCommand.call();
                break;
        }
    }

    public void themeChanged(int theme) {
        if (mApp.getAppliedTheme() == theme) {
            // If same theme, do nothing
            return;
        }
        mApp.applyTheme(theme);
        mChangeThemeCommand.call();
    }

    public int getTheme() {
        return mApp.getAppliedTheme();
    }

    /**
     * @return true if the back was processed by viewmodel
     */
    public boolean backPressed() {
        switch (mLoginStepLiveData.getValue()) {
            case CREDENTIALS:
                mLoginStepLiveData.setValue(LoginStep.ORG);
                return true;
            case INFO:
                mLoginStepLiveData.setValue(LoginStep.THEME);
                return true;
        }
        return false;
    }


    public void passwordChanged(String password) {
        mPassword = password;
    }

    public void emailChanged(String email) {
        mEmail = email;
    }

    public void login() {
        if(!Utils.isValidInput(mEmail)) {
            mSnackBarText.setValue("Please enter a correct email");
            return;
        }
        if(!Utils.isValidPassword(mPassword)) {
            mSnackBarText.setValue("Invalid Password");
            return;
        }
        mCredentialsLiveData.setValue(new Pair<>(mEmail, mPassword));
    }


    public MutableLiveData<Pair<String, String>> getCredentialsLiveData() {
        return mCredentialsLiveData;
    }

    protected SingleLiveEvent<String> getSnackBarText() {
        return mSnackBarText;
    }

    public MutableLiveData<LoginStep> getLoginStepLiveData() {
        return mLoginStepLiveData;
    }

    public void setLoginStepMutableLiveData(MutableLiveData<LoginStep> loginStepMutableLiveData) {
        mLoginStepLiveData = loginStepMutableLiveData;
    }

    public MutableLiveData<String> getUrlMessage() {
        return mUrlMessage;
    }

    public LiveData<Resource<String>> getOrganisationLogoResource() {
        return mOrganisationLogoResource;
    }

    public MediatorLiveData<String> getNextButtonText() {
        return mNextButtonText;
    }

    public SingleLiveEvent<Void> getHomeScreenCommand() {
        return mHomeScreenCommand;
    }

    public SingleLiveEvent<Integer> getChangeThemeCommand() {
        return mChangeThemeCommand;
    }
}
