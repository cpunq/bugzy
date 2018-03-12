package com.bluestacks.bugzy.data.model;


import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import java.util.List;

@Entity(primaryKeys = "filter")
public class FilterCasesResult {
    @NonNull
    private String filter;
    private List<Integer> caseIds;

    public FilterCasesResult(String filter, List<Integer> caseIds) {
        this.filter = filter;
        this.caseIds = caseIds;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<Integer> getCaseIds() {
        return caseIds;
    }

    public void setCaseIds(List<Integer> caseIds) {
        this.caseIds = caseIds;
    }
}
