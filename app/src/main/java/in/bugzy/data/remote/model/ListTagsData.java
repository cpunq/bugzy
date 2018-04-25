package in.bugzy.data.remote.model;


import com.google.gson.annotations.SerializedName;

import in.bugzy.data.model.Tag;

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
