package com.bluestacks.bugzy.data.local.db;


import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.FilterCasesResult;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public abstract class CaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Case... cases);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCases(List<Case> cases);

    @Query("SELECT * FROM `Case` WHERE ixBug in (:ids)")
    public abstract LiveData<List<Case>> loadCasesById(List<Integer> ids);

    @Query("SELECT * from `Case` WHERE ixBug = :id")
    public abstract LiveData<Case> loadCaseById(int id);

    @Query("SELECT * FROM FilterCasesResult WHERE filter = :filter")
    public abstract LiveData<FilterCasesResult> loadCasesForFilter(String filter);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FilterCasesResult filterCases);
}
