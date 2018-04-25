package in.bugzy.data.remote.model;

import com.google.gson.annotations.SerializedName;

import in.bugzy.data.model.Case;

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

