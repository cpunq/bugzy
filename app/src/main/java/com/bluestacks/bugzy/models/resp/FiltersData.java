package com.bluestacks.bugzy.models.resp;


import java.util.List;

public class FiltersData {
    private String type;

    private List<Filter> filters;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }
}

