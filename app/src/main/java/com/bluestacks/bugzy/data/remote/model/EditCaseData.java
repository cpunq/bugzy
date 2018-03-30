package com.bluestacks.bugzy.data.remote.model;

import com.google.gson.annotations.SerializedName;

import com.bluestacks.bugzy.data.model.Case;

public class EditCaseData {
    @SerializedName("case")
    private Case mCase;

    public Case getCase() {
        return mCase;
    }

    public void setCase(Case aCase) {
        mCase = aCase;
    }
}

