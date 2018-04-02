package com.bluestacks.bugzy.ui.splash;



import com.bluestacks.bugzy.data.Repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;

import javax.inject.Inject;

public class SplashViewModel extends ViewModel {
    private LiveData<String> mTokenLiveData;
    private MutableLiveData<Void> mFetchTokenInfo = new MutableLiveData<>();
    private Repository mRepository;
    private Handler mHandler;

    @Inject
    SplashViewModel(Repository repository) {
        mRepository = repository;
        mHandler = new Handler();

        mTokenLiveData = Transformations.switchMap(mFetchTokenInfo, v -> {
            return mRepository.getToken();
        });
    }

    public void splashDisplayed() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFetchTokenInfo.setValue(null);
            }
        }, 3000);
    }

    public LiveData<String> getTokenLiveData() {
        return mTokenLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mHandler.removeCallbacksAndMessages(null);
    }
}
