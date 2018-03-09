package com.bluestacks.bugzy.di.module;

import com.bluestacks.bugzy.data.local.db.BugzyDb;
import com.bluestacks.bugzy.data.local.db.CaseDao;
import com.bluestacks.bugzy.ui.casedetails.CaseDetailsFragment;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provide application-level dependencies
 */
@Module
public class AppModule {
    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    BugzyDb provideDb(Application app) {
        return Room.databaseBuilder(app, BugzyDb.class, "bugzy.db").build();
    }

    @Provides
    @Singleton
    CaseDao provideCaseDao(BugzyDb db) {
        return db.caseDao();
    }
}
