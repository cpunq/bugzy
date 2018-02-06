package com.bluestacks.bugzy.models.resp;


import java.util.List;

public class FiltersData {
    private String type;

    private List<String> filters;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }
}

