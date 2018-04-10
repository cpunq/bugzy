package com.bluestacks.bugzy.data.remote.model;


import com.google.gson.annotations.SerializedName;

import com.bluestacks.bugzy.data.model.Attachment;

import android.net.Uri;

import java.util.List;

public class CaseEditRequest {
    @SerializedName("ixBug")
    private int bugId;

    @SerializedName("sTitle")
    private String title;

    @SerializedName("requiredxmergexin")
    private String requiredMergeIn;

    @SerializedName("productxversion")
    private String foundIn;

    @SerializedName("verifiedxin")
    private String verifiedIn;

    @SerializedName("fixedxin")
    private String fixedIn;

    @SerializedName( "ixPriority")
    int priority;

    @SerializedName( "ixFixFor")
    int fixForId;

    @SerializedName( "ixProject")
    int projectId;

    @SerializedName( "ixArea")
    int projectAreaId;

    @SerializedName( "ixStatus")
    int statusId;

    @SerializedName( "ixPersonAssignedTo")
    int personAssignedToId;

    @SerializedName( "ixCategory")
    int categoryId;

    @SerializedName("sTags")
    List<String> tags;

    @SerializedName("nFileCount")
    int fileCount;

    @SerializedName("sEvent")
    private String eventText;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    private transient List<Attachment> mAttachments;

    public List<Attachment> getAttachments() {
        return mAttachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        mAttachments = attachments;
    }

    public int getBugId() {
        return bugId;
    }

    public void setBugId(int bugId) {
        this.bugId = bugId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequiredMergeIn() {
        return requiredMergeIn;
    }

    public void setRequiredMergeIn(String requiredMergeIn) {
        this.requiredMergeIn = requiredMergeIn;
    }

    public String getFoundIn() {
        return foundIn;
    }

    public void setFoundIn(String foundIn) {
        this.foundIn = foundIn;
    }

    public String getVerifiedIn() {
        return verifiedIn;
    }

    public void setVerifiedIn(String verifiedIn) {
        this.verifiedIn = verifiedIn;
    }

    public String getFixedIn() {
        return fixedIn;
    }

    public void setFixedIn(String fixedIn) {
        this.fixedIn = fixedIn;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getFixForId() {
        return fixForId;
    }

    public void setFixForId(int fixForId) {
        this.fixForId = fixForId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getProjectAreaId() {
        return projectAreaId;
    }

    public void setProjectAreaId(int projectAreaId) {
        this.projectAreaId = projectAreaId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getPersonAssignedToId() {
        return personAssignedToId;
    }

    public void setPersonAssignedToId(int personAssignedToId) {
        this.personAssignedToId = personAssignedToId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getEventText() {
        return eventText;
    }

    public void setEventText(String eventText) {
        this.eventText = eventText;
    }
}
