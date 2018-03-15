package com.bluestacks.bugzy.di.module;

import com.bluestacks.bugzy.data.SearchSuggestionRepository;
import com.bluestacks.bugzy.data.local.db.BugzyDb;
import com.bluestacks.bugzy.data.local.db.CaseDao;
import com.bluestacks.bugzy.data.local.db.MiscDao;
import com.bluestacks.bugzy.data.model.SearchSuggestion;
import com.bluestacks.bugzy.utils.AppExecutors;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;

import static com.bluestacks.bugzy.data.SearchSuggestionRepository.SearchSuggestionType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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


    private List<String> getOrderringOptions() {
        List<String> options = new ArrayList<>();
        options.add("area");
        options.add("priority");
        options.add("milestone");
        options.add("category");
        options.add("status");
        options.add("lastEdited");
        return options;
    }

    @Provides
    @Singleton
    BugzyDb provideDb(Application app, AppExecutors executors) {
        mDb = Room.databaseBuilder(app, BugzyDb.class, "bugzy.db")
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<SearchSuggestion> suggestions = new ArrayList<>();

                                List<String> orderingOptions = getOrderringOptions();
                                for (String option : orderingOptions) {
                                    String text = "orderBy:"+option;
                                    String id = "orderby:"+option;
                                    suggestions.add(new SearchSuggestion(id, text, SearchSuggestionType.ORDER_BY));
                                }

                                for (int i = 1 ; i < 8 ; i++) {
                                    String text = "priority:"+i;
                                    suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.PRIORITY));
                                }

                                String text = "status:active";
                                suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.STATUS));
                                text = "status:closed";
                                suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.STATUS));
                                text = "status:open";
                                suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.STATUS));
                                text = "status:resolved";
                                suggestions.add(new SearchSuggestion(text, text, SearchSuggestionType.STATUS));

                                mDb.miscDao().insertSearchSuggestions(suggestions);
                            }
                        });
                    }
                })
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
}
