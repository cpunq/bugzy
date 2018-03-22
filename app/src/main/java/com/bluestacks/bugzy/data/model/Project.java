package com.bluestacks.bugzy.data.model;


import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Entity;

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
}
