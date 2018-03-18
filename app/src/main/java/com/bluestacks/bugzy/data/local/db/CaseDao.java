package com.bluestacks.bugzy.data.local.db;


import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.FilterCasesResult;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public abstract class CaseDao {
    public static final String TAG = CaseDao.class.getName();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(Case kase);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract int update(Case kase);

    @Query("UPDATE `Case` SET title = :title, priority = :priority, fixFor = :fixFor, projectName = :project, projectArea = :area, " +
            "status = :status, personAssignedTo = :personAssignedTo, personOpenedBy = :personOpenedBy, favorite = :favorite WHERE ixBug = :id")
    public abstract int updatePartial(String title, int priority, String fixFor, String project, String area,
                                      String status, String personAssignedTo, String personOpenedBy, boolean favorite, int id);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long[] insertCases(List<Case> cases);

    @Update(onConflict = OnConflictStrategy.FAIL)
    public abstract int updateCases(List<Case> cases);

    @Transaction
    public void upsert(Case kase) {
        if (insert(kase) == -1) {
            update(kase);
        }
    }

    /**
     * It does a partial UPDATE in case the case is already present
     * @param cases
     */
    @Transaction
    public void upsertCases(List<Case> cases) {
        long[] ids = insertCases(cases);
        int i = 0;
        for (long id: ids) {
            if (id == -1) {
                // cautiously update
                Case kase = cases.get(i);
                updatePartial(kase.getTitle(), kase.getPriority(), kase.getFixFor(), kase.getProjectName(), kase.getProjectArea(),
                        kase.getStatus(), kase.getPersonAssignedTo(), kase.getPersonOpenedBy(), kase.isFavorite(), kase.getIxBug());
            }
            i++;
        }
    }

    @Query("SELECT * FROM `Case` WHERE ixBug in (:ids)")
    public abstract LiveData<List<Case>> loadCasesById(List<Integer> ids);

    @Query("SELECT * from `Case` WHERE ixBug = :id")
    public abstract LiveData<Case> loadCaseById(int id);

    @Query("SELECT * FROM FilterCasesResult WHERE filter = :filter")
    public abstract LiveData<FilterCasesResult> loadCasesForFilter(String filter);


    @Transaction
    public void upsertFilterCaseIds(FilterCasesResult filterCasesResult) {
        if (insert(filterCasesResult) == -1) {
            updateCaseIds(filterCasesResult.getFilter(), BugzyTypeConverters.stringFromIntegerList(filterCasesResult.getCaseIds()));
        }
    }

    @Query("UPDATE `FilterCasesResult` SET caseIds = :caseIds WHERE filter = :filterId")
    public abstract void updateCaseIds(String filterId, String caseIds);

    @Query("UPDATE FilterCasesResult SET appliedSortOrders = :sortOrders WHERE filter = :filterId")
    public abstract void updateSortOrders(String filterId, String sortOrders);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(FilterCasesResult filterCases);
}
