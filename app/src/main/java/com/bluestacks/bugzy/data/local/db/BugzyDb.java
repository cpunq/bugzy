package com.bluestacks.bugzy.data.local.db;


import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.FilterCasesResult;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Case.class, FilterCasesResult.class}, version = 1)
@TypeConverters(BugzyTypeConverters.class)
public abstract class BugzyDb extends RoomDatabase {
    abstract public CaseDao caseDao();
}
