package com.bluestacks.bugzy.models.resp;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ListCasesData {
    private String description;
    private List<Case> cases;
    private int count;
    private int totalHits;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    @NonNull
    public List<Integer> getCaseIds() {
        List<Integer> caseIds = new ArrayList<>();
        for (Case c : getCases()) {
            caseIds.add(c.getIxBug());
        }
        return caseIds;
    }
}

