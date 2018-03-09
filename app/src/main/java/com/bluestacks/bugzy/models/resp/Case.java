package com.bluestacks.bugzy.models.resp;

import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Entity;

import java.io.Serializable;
import java.util.List;


@Entity(primaryKeys = "ixBug")
public class Case implements Serializable {
    int ixBug;

    @SerializedName( "operations")
    List<String> operations;

    @SerializedName("sTitle")
    String title;

    @SerializedName( "ixPriority")
    int priority;

    @SerializedName( "sFixFor")
    String fixFor;

    @SerializedName( "sProject")
    String projectName;

    @SerializedName( "sArea")
    String projectArea;

    @SerializedName( "sStatus")
    String status;

    @SerializedName( "sPersonAssignedTo")
    String personAssignedTo;

    @SerializedName( "sPersonOpenedBy")
    String personOpenedBy;

    @SerializedName( "events")
    List<CaseEvent> caseevents;

    @SerializedName("sFavorite")
    boolean favorite;

    public int getIxBug() {
        return ixBug;
    }

    public void setIxBug(int ixBug) {
        this.ixBug = ixBug;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getFixFor() {
        return fixFor;
    }

    public void setFixFor(String fixFor) {
        this.fixFor = fixFor;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(String projectArea) {
        this.projectArea = projectArea;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPersonAssignedTo() {
        return personAssignedTo;
    }

    public void setPersonAssignedTo(String personAssignedTo) {
        this.personAssignedTo = personAssignedTo;
    }

    public String getPersonOpenedBy() {
        return personOpenedBy;
    }

    public void setPersonOpenedBy(String personOpenedBy) {
        this.personOpenedBy = personOpenedBy;
    }

    public List<CaseEvent> getCaseevents() {
        return caseevents;
    }

    public void setCaseevents(List<CaseEvent> caseevents) {
        this.caseevents = caseevents;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
