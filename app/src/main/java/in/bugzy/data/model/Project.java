package in.bugzy.data.model;


import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;

@Entity(primaryKeys = "id")
public class Project {
    @SerializedName("ixProject")
    private int id;

    @SerializedName("sProject")
    private String project;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return project;
    }

    public static Project createfromCase(Case caseDetails) {
        Project p = new Project();
        p.setId(caseDetails.getProjectId());
        p.setProject(caseDetails.getProjectName());
        return p;
    }
}
