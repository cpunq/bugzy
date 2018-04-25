package in.bugzy.data.remote.model;


import com.google.gson.annotations.SerializedName;

import in.bugzy.data.model.Milestone;

import java.util.List;

public class ListMilestonesData {
    @SerializedName("fixfors")
    private List<Milestone> milestones;

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }
}

