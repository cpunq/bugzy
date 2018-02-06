package com.bluestacks.bugzy.models.resp;

import com.google.gson.annotations.SerializedName;

import com.bluestacks.bugzy.models.Request;

public class ListCasesRequest extends Request {
    String[] cols;

    @SerializedName("sFilter")
    String filter;

    public ListCasesRequest(String[] cols, String filter) {
        super("listCases");
        this.cols = cols;
        this.filter = filter;
    }

    public ListCasesRequest(String[] cols) {
        this(cols, "");
    }

    public String[] getCols() {
        return cols;
    }

    public void setCols(String[] cols) {
        this.cols = cols;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
