package com.bluestacks.bugzy.data.remote.model;


import com.google.gson.annotations.SerializedName;

import com.bluestacks.bugzy.data.model.Tag;

import java.util.List;

public class ListTagsData {
    @SerializedName("tags")
    private List<Tag> mTags;

    public List<Tag> getTags() {
        return mTags;
    }

    public void setTags(List<Tag> tags) {
        mTags = tags;
    }
}
