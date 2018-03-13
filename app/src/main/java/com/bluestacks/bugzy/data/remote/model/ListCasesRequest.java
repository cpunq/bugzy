package com.bluestacks.bugzy.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class ListCasesRequest extends Request {
    String[] cols;

    @SerializedName("sFilter")
    String filter;

    private int max = 200;

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

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
