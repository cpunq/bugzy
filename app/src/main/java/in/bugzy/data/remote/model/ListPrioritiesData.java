package in.bugzy.data.remote.model;


import com.google.gson.annotations.SerializedName;

import in.bugzy.data.model.Priority;

import java.util.List;

public class ListPrioritiesData {
    @SerializedName("priorities")
    private List<Priority> mPriorities;

    public List<Priority> getPriorities() {
        return mPriorities;
    }

    public void setPriorities(List<Priority> priorities) {
        mPriorities = priorities;
    }
}

