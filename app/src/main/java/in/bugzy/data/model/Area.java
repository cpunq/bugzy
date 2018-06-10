package in.bugzy.data.model;


import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;

@Entity(primaryKeys = "id")
public class Area {
    @SerializedName("ixArea")
    private int id;

    @SerializedName("sArea")
    private String area;

    @SerializedName("ixProject")
    private int projectId;

    @SerializedName("sProject")
    private String project;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return area;
    }

    public static Area createfromCase(Case caseDetails) {
        Area a = new Area();
        a.setProjectId(caseDetails.getProjectId());
        a.setProject(caseDetails.getProjectName());
        a.setId(caseDetails.getProjectAreaId());
        a.setArea(caseDetails.getProjectArea());
        return a;
    }
}
