package com.bluestacks.bugzy.data.remote.model;


import com.google.gson.annotations.SerializedName;

import com.bluestacks.bugzy.data.model.CaseStatus;

import java.util.List;

public class ListStatusesData {
    @SerializedName("statuses")
    private List<CaseStatus> mStatuses;

    public List<CaseStatus> getStatuses() {
        return mStatuses;
    }

    public void setStatuses(List<CaseStatus> statuses) {
        mStatuses = statuses;
    }
}

