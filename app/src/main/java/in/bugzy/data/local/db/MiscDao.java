package in.bugzy.data.local.db;


import in.bugzy.data.model.Area;
import in.bugzy.data.model.CaseStatus;
import in.bugzy.data.model.Category;
import in.bugzy.data.model.Milestone;
import in.bugzy.data.model.Person;
import in.bugzy.data.model.Priority;
import in.bugzy.data.model.Project;
import in.bugzy.data.model.RecentSearch;
import in.bugzy.data.model.SearchSuggestion;
import in.bugzy.data.model.Tag;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

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
    public abstract void insertTags(List<Tag> tags);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCategories(List<Category> categories);

    @Query("SELECT * from `Milestone`")
    public abstract LiveData<List<Milestone>> loadMilestones();

    @Query("SELECT * from `Milestone` WHERE projectId = :projectId OR projectId = 0")
    public abstract LiveData<List<Milestone>> loadMilestones(int projectId);

    @Query("SELECT * from `Area` ORDER BY area")
    public abstract LiveData<List<Area>> loadAreas();

    @Query("SELECT * from `Area` WHERE projectId = :projectId ORDER BY area")
    public abstract LiveData<List<Area>> loadAreas(int projectId);

    @Query("SELECT * from `Project` ORDER BY project")
    public abstract LiveData<List<Project>> loadProjects();

    @Query("SELECT * from `CaseStatus`")
    public abstract LiveData<List<CaseStatus>> loadStatuses();

    @Query("SELECT * from `CaseStatus` WHERE category = :categoryId")
    public abstract LiveData<List<CaseStatus>> loadStatuses(int categoryId);

    @Query("SELECT * from `Priority`")
    public abstract LiveData<List<Priority>> loadPriorities();

    @Query("SELECT * from `Tag`")
    public abstract LiveData<List<Tag>> loadTags();

    @Query("SELECT * from `Tag` WHERE name LIKE :query")
    public abstract LiveData<List<Tag>> searchTags(String query);

    @Query("SELECT * from `Category` ORDER BY name")
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
