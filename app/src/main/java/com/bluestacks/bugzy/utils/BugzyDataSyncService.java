package com.bluestacks.bugzy.utils;

import com.bluestacks.bugzy.data.Repository;

import android.arch.lifecycle.LifecycleService;
import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class BugzyDataSyncService extends LifecycleService {
    public static final String TAG = BugzyDataSyncService.class.getName();
    @Inject
    Repository mRepository;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        sync();
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    public void sync() {
        mRepository.getAreasLiveData().observe(this, value -> {
            Log.d(TAG, "received areas" + value.status);
        });
        mRepository.getMilestonesLiveData().observe(this, value -> {
            Log.d(TAG, "received miletsones" + value.status);
        });
        mRepository.getProjectsLiveData().observe(this, value -> {
            Log.d(TAG, "received projects" + value.status);
        });
        // After all 3 are received, call this.stopSelf
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " onDestroy()");
        super.onDestroy();
    }
}
