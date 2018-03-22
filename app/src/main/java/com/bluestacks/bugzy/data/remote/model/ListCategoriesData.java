package com.bluestacks.bugzy.data.remote.model;


import com.google.gson.annotations.SerializedName;

import com.bluestacks.bugzy.data.model.Category;

import java.util.List;

public class ListCategoriesData {
    @SerializedName("categories")
    private List<Category> mCategories;

    public List<Category> getCategories() {
        return mCategories;
    }

    public void setCategories(List<Category> categories) {
        mCategories = categories;
    }
}

