package com.bluestacks.bugzy.data.local.db;


import com.bluestacks.bugzy.data.model.Area;
import com.bluestacks.bugzy.data.model.CaseStatus;
import com.bluestacks.bugzy.data.model.Category;
import com.bluestacks.bugzy.data.model.Milestone;
import com.bluestacks.bugzy.data.model.Person;
import com.bluestacks.bugzy.data.model.Priority;
import com.bluestacks.bugzy.data.model.Project;
import com.bluestacks.bugzy.data.model.RecentSearch;
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertStatuses(List<CaseStatus> statuses);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPriorities(List<Priority> priorities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCategories(List<Category> categories);

    @Query("SELECT * from `Milestone`")
    public abstract LiveData<List<Milestone>> loadMilestones();

    @Query("SELECT * from `Milestone` WHERE projectId = :projectId OR projectId = 0")
    public abstract LiveData<List<Milestone>> loadMilestones(int projectId);

    @Query("SELECT * from `Area`")
    public abstract LiveData<List<Area>> loadAreas();

    @Query("SELECT * from `Area` WHERE projectId = :projectId")
    public abstract LiveData<List<Area>> loadAreas(int projectId);

    @Query("SELECT * from `Project`")
    public abstract LiveData<List<Project>> loadProjects();

    @Query("SELECT * from `CaseStatus`")
    public abstract LiveData<List<CaseStatus>> loadStatuses();

    @Query("SELECT * from `Priority`")
    public abstract LiveData<List<Priority>> loadPriorities();

    @Query("SELECT * from `Category`")
    public abstract LiveData<List<Category>> loadCategories();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertSearchSuggestions(List<SearchSuggestion> suggestions);

    @Query("SELECT * FROM `SearchSuggestion` WHERE id LIKE :query")
    public abstract LiveData<List<SearchSuggestion>> loadSearchSuggestions(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPersons(List<Person> personList);

    @Query("SELECT * FROM Person ORDER BY fullname ASC")
    public abstract LiveData<List<Person>> loadPersons();

    @Query("SELECT * FROM RecentSearch ORDER BY createdAt DESC LIMIT 50")
    public abstract LiveData<List<RecentSearch>> loadRecentSearches();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(RecentSearch search);
}
