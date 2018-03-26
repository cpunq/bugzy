package com.bluestacks.bugzy.data.local.db;


import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.CaseStatus;
import com.bluestacks.bugzy.data.model.Category;
import com.bluestacks.bugzy.data.model.FilterCasesResult;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.data.model.Priority;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.data.model.RecentSearch;
import com.bluestacks.bugzy.data.model.SearchSuggestion;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Case.class, FilterCasesResult.class, Area.class, Milestone.class, Project.class, SearchSuggestion.class, Person.class,
        Priority.class, Category.class, CaseStatus.class, RecentSearch.class}, version = 1)
@TypeConverters(BugzyTypeConverters.class)
public abstract class BugzyDb extends RoomDatabase {
    abstract public CaseDao caseDao();
    abstract public MiscDao miscDao();
}
