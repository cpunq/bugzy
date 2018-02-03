package com.bluestacks.bugzy.models.resp;

import com.google.gson.annotations.SerializedName;

public class Attachment {
    @SerializedName("sFileName")
    String filename;

    @SerializedName("sURL")
    String url;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
