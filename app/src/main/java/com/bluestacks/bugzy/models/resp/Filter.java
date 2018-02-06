package com.bluestacks.bugzy.models.resp;


import com.google.gson.annotations.SerializedName;

public class Filter {
    private String type;

    @SerializedName("sFilter")
    private String filter;

    private String text;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
