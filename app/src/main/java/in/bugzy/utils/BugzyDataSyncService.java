package in.bugzy.utils;

import in.bugzy.data.Repository;

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
        mRepository.getAreas(true).observe(this, value -> {
            Log.d(TAG, "received areas" + value.status);
        });
        mRepository.getMilestones(true).observe(this, value -> {
            Log.d(TAG, "received miletsones" + value.status);
        });
        mRepository.getProjects(true).observe(this, value -> {
            Log.d(TAG, "received projects" + value.status);
        });
        mRepository.getPeople(true).observe(this, value -> {
            Log.d(TAG, "received people" + value.status);
        });
        mRepository.getStatuses(true).observe(this, value -> {
            Log.d(TAG, "received statuses" + value.status);
        });
        mRepository.getPriorities(true).observe(this, value -> {
            Log.d(TAG, "received priorities" + value.status);
        });
        mRepository.getCategories(true).observe(this, value -> {
            Log.d(TAG, "received categories" + value.status);
        });
        mRepository.getTags(true).observe(this, value -> {
            Log.d(TAG, "received tags" + value.status);
        });
        // After all 8 are received, call this.stopSelf
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " onDestroy()");
        super.onDestroy();
    }
}
