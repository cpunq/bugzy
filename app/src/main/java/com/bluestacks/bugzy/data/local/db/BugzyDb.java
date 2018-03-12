package com.bluestacks.bugzy.data.local.db;


import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.FilterCasesResult;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Case.class, FilterCasesResult.class}, version = 1)
@TypeConverters(BugzyTypeConverters.class)
public abstract class BugzyDb extends RoomDatabase {
    abstract public CaseDao caseDao();
}
