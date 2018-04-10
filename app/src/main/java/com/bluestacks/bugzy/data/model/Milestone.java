package com.bluestacks.bugzy.data.model;


import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Entity;

@Entity(primaryKeys = "id")
public class Milestone {
    @SerializedName("ixFixFor")
    private int id;

    @SerializedName("sFixFor")
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return ("".equals(project.trim()) ? "All Projects" : project) +": " + name;
    }

    public static Milestone createfromCase(Case caseDetails) {
        Milestone m = new Milestone();
        m.setProjectId(caseDetails.getProjectId());
        m.setProject(caseDetails.getProjectName());
        m.setId(caseDetails.getFixForId());
        m.setName(caseDetails.getFixFor());
        return m;
    }
}
