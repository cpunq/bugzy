package com.bluestacks.bugzy.data.model;

import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Ignore;
import android.net.Uri;

import java.io.Serializable;

public class Attachment implements Serializable {
    @SerializedName("sFileName")
    String filename;

    @SerializedName("sURL")
    String url;

    @Ignore
    Uri uri;

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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
