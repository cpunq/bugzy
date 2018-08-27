package in.bugzy.di.module;

import in.bugzy.data.local.db.BugzyDb;
import in.bugzy.data.local.db.CaseDao;
import in.bugzy.data.local.db.MiscDao;
import in.bugzy.utils.AppExecutors;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.bugzy.utils.BugzyUrlGenerator;

/**
 * Provide application-level dependencies
 */
@Module
public class AppModule {
    Application mApplication;
    BugzyDb mDb;

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
    BugzyDb provideDb(Application app, AppExecutors executors) {
        mDb = Room.databaseBuilder(app, BugzyDb.class, "bugzy.db")
                .fallbackToDestructiveMigration()
                .build();
        return mDb;
    }

    @Provides
    @Singleton
    CaseDao provideCaseDao(BugzyDb db) {
        return db.caseDao();
    }

    @Provides
    @Singleton
    MiscDao provideMiscDao(BugzyDb db) {
        return db.miscDao();
    }

    @Provides
    @Singleton
    BugzyUrlGenerator provideUrlGenerator() {
        return new BugzyUrlGenerator("", 0, "");
    }
}
