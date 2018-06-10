package in.bugzy.data.local.db;


import in.bugzy.data.model.Area;
import in.bugzy.data.model.Case;
import in.bugzy.data.model.CaseStatus;
import in.bugzy.data.model.Category;
import in.bugzy.data.model.FilterCasesResult;
import in.bugzy.data.model.Milestone;
import in.bugzy.data.model.Person;
import in.bugzy.data.model.Priority;
import in.bugzy.data.model.Project;
import in.bugzy.data.model.RecentSearch;
import in.bugzy.data.model.SearchSuggestion;
import in.bugzy.data.model.Tag;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Case.class, FilterCasesResult.class, Area.class, Milestone.class, Project.class, SearchSuggestion.class, Person.class,
        Priority.class, Category.class, CaseStatus.class, RecentSearch.class, Tag.class}, version = 2)
@TypeConverters(BugzyTypeConverters.class)
public abstract class BugzyDb extends RoomDatabase {
    abstract public CaseDao caseDao();
    abstract public MiscDao miscDao();
}
