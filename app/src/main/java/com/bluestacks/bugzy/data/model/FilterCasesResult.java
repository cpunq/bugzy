package com.bluestacks.bugzy.data.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import java.util.List;

@Entity(primaryKeys = "filter")
public class FilterCasesResult {
    @NonNull
    private String filter;
    private List<Integer> caseIds;
    private List<String> appliedSortOrders;

    @Ignore
    private List<Case> cases;

    public FilterCasesResult(String filter, List<Integer> caseIds, List<String> appliedSortOrders) {
        this.filter = filter;
        this.caseIds = caseIds;
        this.appliedSortOrders = appliedSortOrders;
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

    public List<String> getAppliedSortOrders() {
        return appliedSortOrders;
    }

    public void setAppliedSortOrders(List<String> appliedSortOrders) {
        this.appliedSortOrders = appliedSortOrders;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }
}
