package com.bluestacks.bugzy.data.local.db;


import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.data.model.SearchSuggestion;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;


@Dao
public abstract class MiscDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Area> areas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertMilestones(List<Milestone> milestones);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertProjects(List<Project> projects);

    @Query("SELECT * from `Milestone`")
    public abstract LiveData<List<Milestone>> loadMilestones();

    @Query("SELECT * from `Area`")
    public abstract LiveData<List<Area>> loadAreas();

    @Query("SELECT * from `Project`")
    public abstract LiveData<List<Project>> loadProjects();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertSearchSuggestions(List<SearchSuggestion> suggestions);

    @Query("SELECT * FROM `SearchSuggestion` WHERE id LIKE :query")
    public abstract LiveData<List<SearchSuggestion>> loadSearchSuggestions(String query);
}
